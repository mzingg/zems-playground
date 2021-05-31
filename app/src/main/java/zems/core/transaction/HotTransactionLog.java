package zems.core.transaction;

import zems.core.contentbus.Content;
import zems.core.utils.BufferUtils;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.*;
import static zems.core.utils.BufferUtils.disposeBuffer;

public class HotTransactionLog implements AutoCloseable {

  private static final int DEFAULT_HEAD_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
  private static final int MINIMAL_HEAD_SIZE = 256;

  private SequenceGenerator sequenceGenerator;
  private int headBufferSize;

  private Path logPath;

  private Path greenHeadPath;
  private Path redHeadPath;

  private HeadState state;

  private FileChannel headChannel;
  private MappedByteBuffer headBuffer;

  public HotTransactionLog() {
    state = HeadState.INIT;
    headBufferSize = DEFAULT_HEAD_BUFFER_SIZE;
  }

  public void open() {
    if (sequenceGenerator == null) {
      throw new IllegalStateException("no SequenceGenerator set");
    }

    try {
      switchHead();
      ensureLogPathExistsAndIsWritable();
    } catch (IOException | NullPointerException e) {
      throw new IllegalStateException(e);
    }
  }

  // TODO: critical function - has to be Thread safe
  private void switchHead() {
    try {
      if (state == HeadState.SWITCHING) {
        throw new IllegalStateException("HotTransaction log is already in switching state");
      }

      // immediately mark this log as in the process of switching
      HeadState previousState = state;
      state = HeadState.SWITCHING;

      // determine the next state
      HeadState newState;
      switch (previousState) {
        case INIT, GREEN -> newState = HeadState.RED;
        case RED -> newState = HeadState.GREEN;
        default -> throw new IllegalStateException("HotTransaction log is in an unknown state");
      }

      // close previous channel
      if (headChannel != null) {
        headChannel.close();
        disposeBuffer(headBuffer);
      }

      // and open and map the new one
      this.headChannel = FileChannel.open(getHeadPathByState(newState), Set.of(CREATE, READ, WRITE, SYNC));
      this.headBuffer = headChannel.map(READ_WRITE, 0, headBufferSize);

      // TODO: update sequence counter from existing data in the log??
      if (previousState == HeadState.INIT) {
      }

      state = newState;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() throws Exception {
    headChannel.close();
  }


  public HotTransactionLog append(Content content) {
    Objects.requireNonNull(content);
    ensureActive();

    TransactionSegment segment = new TransactionSegment()
        .setPath(content.path())
        .setSequenceId(sequenceGenerator.next())
        .setData(content.properties());

    try {

      writeSegment(segment);

    } catch (BufferOverflowException headIsFulll) {

      // cleanup the already written portion of the buffer with an array of zeros
      headBuffer.reset(); // go to the position marked before the last write
      byte[] cleanup = new byte[headBuffer.remaining()];
      headBuffer.put(cleanup);
      headBuffer.reset();
      int lastGoodPosition = headBuffer.position();

      switchHead();
      writeSegment(segment); // to the new head

      // commit inactive head to log
      Path otherPath = getOtherHeadPathByCurrentState();
      try (
          FileChannel inactiveChannel = FileChannel.open(otherPath, Set.of(READ, WRITE));
          FileChannel logChannel = FileChannel.open(logPath, Set.of(APPEND))
      ) {
        //logChannel.position(logChannel.size());
        inactiveChannel.transferTo(0, lastGoodPosition, logChannel);

        ByteBuffer zeros = ByteBuffer.allocate(headBufferSize);
        inactiveChannel.position(0);
        inactiveChannel.write(zeros);
      } catch (IOException ioException) {
        throw new IllegalStateException(ioException);
      }
    }

    return this;
  }

  private void writeSegment(TransactionSegment segment) {
    headBuffer.mark(); // remeber last 'good' position in case we have to revert write
    headBuffer.put(BufferUtils.RECORD_SEPARATOR_BYTE);
    segment.pack(headBuffer);
  }

  public HotTransactionLog setSequenceGenerator(SequenceGenerator sequenceGenerator) {
    Objects.requireNonNull(sequenceGenerator);

    this.sequenceGenerator = sequenceGenerator;
    return this;
  }

  public HotTransactionLog setHeadBufferSize(int headBufferSize) {
    if (headBufferSize < MINIMAL_HEAD_SIZE) {
      throw new IllegalArgumentException("headBufferSize must be at least 256 bytes  - default value is (2MB)");
    }

    this.headBufferSize = headBufferSize;
    return this;
  }

  public HotTransactionLog setLogPath(Path logPath) {
    Objects.requireNonNull(logPath);

    this.logPath = logPath;
    return this;
  }

  public HotTransactionLog setGreenHeadPath(Path greenHeadPath) {
    Objects.requireNonNull(greenHeadPath);

    this.greenHeadPath = greenHeadPath;
    return this;
  }

  public HotTransactionLog setRedHeadPath(Path redHeadPath) {
    Objects.requireNonNull(redHeadPath);

    this.redHeadPath = redHeadPath;
    return this;
  }

  private void ensureActive() {
    if (headBuffer == null || headChannel == null || !headChannel.isOpen()) {
      throw new IllegalStateException("HotTransactionLog is not active - please call open() first");
    }
  }

  private void ensureLogPathExistsAndIsWritable() throws IOException {
    if (Files.exists(logPath, NOFOLLOW_LINKS)) {
      if (!Files.isRegularFile(logPath, NOFOLLOW_LINKS) || !Files.isWritable(logPath)) {
        throw new IllegalStateException("logPath(" + logPath + ") exists but is not writable");
      }
    } else {
      Files.createFile(logPath);
    }
  }

  private Path getHeadPathByState(HeadState desiredState) {
    if (desiredState != HeadState.RED && desiredState != HeadState.GREEN) {
      throw new IllegalStateException("HotTransactionLog unsupported state (" + desiredState + ") for getHeadPathByState");
    }
    return desiredState == HeadState.RED ? redHeadPath : greenHeadPath;
  }

  private Path getOtherHeadPathByCurrentState() {
    if (state != HeadState.RED && state != HeadState.GREEN) {
      throw new IllegalStateException("HotTransactionLog unsupported state (" + state + ") for getOtherHeadPathByCurrentState");
    }
    return state == HeadState.RED ? greenHeadPath : redHeadPath;
  }

  private enum HeadState {
    INIT, SWITCHING, RED, GREEN
  }
}
