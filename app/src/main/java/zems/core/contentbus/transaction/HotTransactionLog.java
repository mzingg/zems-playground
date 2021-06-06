package zems.core.contentbus.transaction;

import zems.core.concept.Content;
import zems.core.concept.SequenceGenerator;
import zems.core.concept.TransactionLogStatistics;
import zems.core.utils.ZemsIoUtils;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.StandardOpenOption.*;
import static zems.core.utils.ZemsIoUtils.disposeBuffer;

public class HotTransactionLog implements AutoCloseable {

  private static final int DEFAULT_HEAD_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
  private static final int MINIMAL_HEAD_SIZE = 256;

  private SequenceGenerator sequenceGenerator;
  private int headBufferSize;

  private MainTransactionLog commitLog;

  private Path greenHeadPath;
  private Path redHeadPath;

  private HeadState state;

  private FileChannel headChannel;
  private MappedByteBuffer headBuffer;

  private final ReentrantLock writeLock = new ReentrantLock();

  private final TransactionLogStatistics stats;

  public HotTransactionLog() {
    this(new TransactionLogStatistics() {
    });
  }

  public HotTransactionLog(TransactionLogStatistics stats) {
    Objects.requireNonNull(stats);

    this.stats = stats.reset();
    this.state = HeadState.INIT;
    this.headBufferSize = DEFAULT_HEAD_BUFFER_SIZE;
  }

  public void open() {
    if (sequenceGenerator == null) {
      throw new IllegalStateException("no SequenceGenerator set");
    }
    if (commitLog == null) {
      throw new IllegalStateException("commitLog must not be null");
    }

    switchHead();
  }

  // TODO: critical function - has to be Thread safe
  private void switchHead() {
    writeLock.lock();
    try {
      if (state == HeadState.SWITCHING) {
        throw new IllegalStateException("HotTransaction log is already in switching state");
      }

      // immediately mark this log as in the process of switching
      HeadState previousState = state;
      state = HeadState.SWITCHING;

      // close previous channel and dispose buffer to make sure that the following file reads are safe
      if (headChannel != null) {
        headChannel.close();
        disposeBuffer(headBuffer);
      }

      // determine the next state
      HeadState newState;
      switch (previousState) {
        case GREEN -> newState = HeadState.RED;
        case RED -> newState = HeadState.GREEN;
        case INIT -> {
          ZemsIoUtils.touch(redHeadPath);
          ZemsIoUtils.touch(greenHeadPath);
          newState = findLastStateFromTransactionFiles();
        }
        default -> throw new IllegalStateException("HotTransaction log is in an unknown state");
      }

      // and map the new one
      this.headChannel = FileChannel.open(getHeadPathByState(newState), Set.of(CREATE, READ, WRITE, SYNC));
      this.headBuffer = headChannel.map(READ_WRITE, 0, headBufferSize);

      state = newState;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      writeLock.unlock();
    }
  }

  private HeadState findLastStateFromTransactionFiles() {
    long redLastPosition = 0L;
    long greenLastPosition = 0L;

    if (Files.exists(redHeadPath) && Files.isReadable(redHeadPath)) {
      try (
          MainTransactionLog redLog = new MainTransactionLog()
              .setSequenceGenerator(sequenceGenerator)
              .setLogPath(redHeadPath)
              .setAllowsSuperfluousData(true)
      ) {
        redLastPosition = redLog.seekLastPosition();
      }
    }

    if (Files.exists(greenHeadPath) && Files.isReadable(greenHeadPath)) {
      try (
          MainTransactionLog greenLog = new MainTransactionLog()
              .setSequenceGenerator(sequenceGenerator)
              .setLogPath(greenHeadPath)
              .setAllowsSuperfluousData(true)
      ) {
        greenLastPosition = greenLog.seekLastPosition();
      }
    }

    if (redLastPosition > 0 && greenLastPosition > 0) {
      throw new IllegalStateException("both head logs contain data");
    }

    // if no log contains any data we default to the RED state
    if (redLastPosition == 0 && greenLastPosition == 0) {
      return HeadState.RED;
    }

    // otherwise to the state that contained the separator byte is returned
    return redLastPosition > 0 ? HeadState.RED : HeadState.GREEN;
  }

  @Override
  public void close() throws Exception {
    writeLock.lock();
    try {
      headBuffer.force();
      headChannel.close();
    } finally {
      writeLock.unlock();
    }
  }

  public HotTransactionLog append(Content content) {
    Objects.requireNonNull(content);
    ensureActive();

    TransactionSegment segment = new TransactionSegment()
        .setPath(content.path())
        .setSequenceId(sequenceGenerator.next())
        .setData(content.properties());

    writeLock.lock();
    try {
      writeSegment(segment);
    } catch (BufferOverflowException headIsFull) {
      try {
        // cleanup the already written portion of the buffer with an array of zeros
        headBuffer.reset(); // go to the position marked before the last write
        byte[] cleanup = new byte[headBuffer.remaining()];
        headBuffer.put(cleanup);
        headBuffer.reset();
        int lastGoodPosition = headBuffer.position();

        switchHead();
        writeSegment(segment); // to the new head

        commitAndCleanInactiveHead(lastGoodPosition);
      } catch (Throwable any) {
        stats.countAppendError();
        throw any;
      }
    } catch (Throwable any) {
      stats.countAppendError();
      throw any;
    } finally {
      writeLock.unlock();
    }

    return this;
  }

  public HeadState getState() {
    return state;
  }

  public HotTransactionLog setSequenceGenerator(SequenceGenerator sequenceGenerator) {
    Objects.requireNonNull(sequenceGenerator);

    this.sequenceGenerator = sequenceGenerator;
    return this;
  }

  public HotTransactionLog setCommitLog(MainTransactionLog commitLog) {
    Objects.requireNonNull(commitLog);

    this.commitLog = commitLog;
    return this;
  }

  public HotTransactionLog setHeadBufferSize(int headBufferSize) {
    if (headBufferSize < MINIMAL_HEAD_SIZE) {
      throw new IllegalArgumentException("headBufferSize must be at least 256 bytes  - default value is (2MB)");
    }

    this.headBufferSize = headBufferSize;
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

  private void writeSegment(TransactionSegment segment) {
    headBuffer.mark(); // remember last 'good' position in case we have to revert write
    headBuffer.put(ZemsIoUtils.RECORD_SEPARATOR_BYTE);
    segment.pack(headBuffer);
    stats.countAppend().countAppendSegments(1).countAppendAmountInBytes(segment.packSize() + 1);
  }

  private void commitAndCleanInactiveHead(int amountOfBytesToKeep) {
    Path inactiveHead = getInactiveHeadPathByCurrentState();
    ZemsIoUtils.appendFileFrom(inactiveHead, commitLog.getLogPath(), amountOfBytesToKeep);
    ZemsIoUtils.zeroFile(inactiveHead);
  }

  private void ensureActive() {
    if (headBuffer == null || headChannel == null || !headChannel.isOpen()) {
      throw new IllegalStateException("HotTransactionLog is not active - please call open() first");
    }
  }

  private Path getHeadPathByState(HeadState desiredState) {
    if (desiredState != HeadState.RED && desiredState != HeadState.GREEN) {
      throw new IllegalStateException("HotTransactionLog unsupported state (" + desiredState + ") for getHeadPathByState");
    }
    return desiredState == HeadState.RED ? redHeadPath : greenHeadPath;
  }

  private Path getInactiveHeadPathByCurrentState() {
    if (state != HeadState.RED && state != HeadState.GREEN) {
      throw new IllegalStateException("HotTransactionLog unsupported state (" + state + ") for getOtherHeadPathByCurrentState");
    }
    return state == HeadState.RED ? greenHeadPath : redHeadPath;
  }

  public enum HeadState {
    INIT, SWITCHING, RED, GREEN
  }
}
