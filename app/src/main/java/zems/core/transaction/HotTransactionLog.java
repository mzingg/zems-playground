package zems.core.transaction;

import zems.core.contentbus.Content;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.*;

public class HotTransactionLog implements AutoCloseable {

  private static final int HEAD_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
  private final SequenceGenerator sequenceGenerator;

  private Path logPath;

  private boolean isActive;

  private Path headPath;
  private FileChannel headChannel;
  private MappedByteBuffer headBuffer;

  public HotTransactionLog(SequenceGenerator sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
    this.isActive = false;
  }

  public void open() {
    try {
      this.headChannel = FileChannel.open(headPath, Set.of(CREATE, READ, WRITE, SYNC));
      this.headBuffer = headChannel.map(READ_WRITE, 0, HEAD_BUFFER_SIZE);

      ensureLogPathExistsAndIsWritable();

      this.isActive = true;
    } catch (IOException | NullPointerException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() throws Exception {
    headChannel.close();
    isActive = false;
  }


  public HotTransactionLog append(Content content) {
    Objects.requireNonNull(content);
    ensureActive();

    TransactionSegment segment = new TransactionSegment()
        .setPath(content.path())
        .setSequenceId(sequenceGenerator.next())
        .setData(content.properties());

    segment.pack(headBuffer);

    return this;
  }

  public HotTransactionLog setLogPath(Path logPath) {
    Objects.requireNonNull(logPath);

    this.logPath = logPath;
    return this;
  }

  public HotTransactionLog setHeadPath(Path headPath) {
    Objects.requireNonNull(headPath);

    this.headPath = headPath;
    return this;
  }

  private void ensureActive() {
    if (!isActive || headBuffer == null || headChannel == null || !headChannel.isOpen()) {
      throw new IllegalStateException("HotTransactionLog is not active - please call enable() first");
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
}
