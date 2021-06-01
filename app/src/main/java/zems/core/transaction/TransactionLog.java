package zems.core.transaction;

import zems.core.contentbus.Content;
import zems.core.utils.ZemsIoUtils;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.*;

public class TransactionLog implements AutoCloseable {

  private static final int DEFAULT_READ_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
  private static final int MINIMAL_READ_BUFFER_SIZE = 256;

  private SequenceGenerator sequenceGenerator;
  private Path logPath;
  private int readBufferSize;
  private boolean allowsSuperfluousData;

  public TransactionLog() {
    this.readBufferSize = DEFAULT_READ_BUFFER_SIZE;
    this.allowsSuperfluousData = false;
  }

  @Override
  public void close() {
  }

  public Stream<Content> read() {
    ensureReady();

    Stream.Builder<Content> builder = Stream.builder();

    try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(READ))) {

      ByteBuffer buffer = ByteBuffer.allocate(readBufferSize);
      int bytesRead = logFileChannel.read(buffer);
      while (bytesRead > 0) {
        buffer.flip(); // set buffer to read mode

        boolean segmentTooLarge = false;
        boolean endReached = false;
        while (buffer.hasRemaining() && !segmentTooLarge && !endReached) {
          buffer.mark(); // mark the beginning of this segment in case we exeed the readBuffer
          byte separatorByte = buffer.get();
          if (separatorByte == ZemsIoUtils.RECORD_SEPARATOR_BYTE) {
            try {
              TransactionSegment segment = new TransactionSegment().unpack(buffer);

              builder.accept(new Content(segment.getPath(), segment.getData()));
            } catch (BufferUnderflowException ignored) {
              // our segment spans over the current read buffer
              buffer.reset(); // reset to the last mark so that the remaining amount is correct
              segmentTooLarge = true;
            }
          } else {
            endReached = true;
            if (!allowsSuperfluousData) {
              throw new IllegalStateException("transaction log contains superflous data");
            }
          }
        }
        if (segmentTooLarge && buffer.hasRemaining()) {
          // rewind the fileChannel read pointer to the beginning of our last too large segment
          logFileChannel.position(logFileChannel.position() - buffer.remaining());
        }

        buffer.clear();
        bytesRead = logFileChannel.read(buffer);
      }

    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }

    return builder.build();
  }

  public TransactionLog append(Content... content) {
    Objects.requireNonNull(content);
    ensureReady();

    List<TransactionSegment> segments = new ArrayList<>();
    for (Content value : content) {
      TransactionSegment segment = new TransactionSegment()
          .setPath(value.path())
          .setSequenceId(sequenceGenerator.next())
          .setData(value.properties());

      segments.add(segment);
    }

    return append(segments.toArray(new TransactionSegment[0]));
  }

  public TransactionLog append(TransactionSegment... segments) {
    Objects.requireNonNull(segments);
    ensureReady();

    int bufferSize = 0;
    for (TransactionSegment segment : segments) {
      bufferSize += segment.packSize();
    }
    bufferSize += segments.length; // separator bytes

    try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(CREATE, APPEND, SYNC))) {
      ByteBuffer logBuffer = ByteBuffer.allocate(bufferSize);

      for (TransactionSegment segment : segments) {
        logBuffer.put(ZemsIoUtils.RECORD_SEPARATOR_BYTE);
        segment.pack(logBuffer);
      }
      logBuffer.flip();

      logFileChannel.write(logBuffer);
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }

    return this;
  }

  public TransactionLog setSequenceGenerator(SequenceGenerator sequenceGenerator) {
    Objects.requireNonNull(sequenceGenerator);

    this.sequenceGenerator = sequenceGenerator;
    return this;
  }

  public Path getLogPath() {
    ensureLogPathExistsAndIsWritable();

    return logPath;
  }

  public TransactionLog setLogPath(Path logPath) {
    Objects.requireNonNull(logPath);

    this.logPath = logPath;
    return this;
  }

  public TransactionLog setReadBufferSize(int readBufferSize) {
    if (readBufferSize < MINIMAL_READ_BUFFER_SIZE) {
      throw new IllegalArgumentException("headBufferSize must be at least 256 bytes  - default value is (2MB)");
    }

    this.readBufferSize = readBufferSize;
    return this;
  }

  /**
   * USE WITH CARE: Should only be used for reading HOT logs in tests.
   *
   * @param allowsSuperfluousData {@link Boolean}
   * @return this
   */
  public TransactionLog setAllowsSuperfluousData(boolean allowsSuperfluousData) {
    this.allowsSuperfluousData = allowsSuperfluousData;
    return this;
  }

  private void ensureReady() {
    if (sequenceGenerator == null) {
      throw new IllegalStateException("sequenceGenerator must not be null");
    }

    ensureLogPathExistsAndIsWritable();
  }

  private void ensureLogPathExistsAndIsWritable() {
    try {
      if (Files.exists(logPath, NOFOLLOW_LINKS)) {
        if (!Files.isRegularFile(logPath, NOFOLLOW_LINKS) || !Files.isWritable(logPath)) {
          throw new IllegalStateException("logPath(" + logPath + ") exists but is not writable");
        }
      } else {
        Files.createFile(logPath);
      }
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }

}
