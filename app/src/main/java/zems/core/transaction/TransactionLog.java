package zems.core.transaction;

import zems.core.contentbus.Content;

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

  private static final int READ_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
  private final SequenceGenerator sequenceGenerator;
  private Path logPath;

  public TransactionLog(SequenceGenerator sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public void close() throws Exception {
  }

  public Stream<Content> read() {
    ensureLogPathExistsAndIsWritable();

    Stream.Builder<Content> builder = Stream.builder();

    try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(READ))) {

      ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
      int bytesRead = logFileChannel.read(buffer);
      while (bytesRead > 0) {
        buffer.flip(); // set buffer to read mode

        boolean segmentTooLarge = false;
        while (buffer.hasRemaining() && !segmentTooLarge) {
          buffer.mark(); // mark the beginning of this segment in case we exeed the readBuffer
          try {
            TransactionSegment segment = new TransactionSegment().unpack(buffer);
            builder.accept(new Content(segment.getPath(), segment.getData()));
          } catch (BufferUnderflowException ignored) {
            // our segment spans over the current read buffer
            buffer.reset(); // reset to the last mark so that the remaining amount is correct
            segmentTooLarge = true;
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
    ensureLogPathExistsAndIsWritable();

    int bufferSize = 0;
    for (TransactionSegment segment : segments) {
      bufferSize += segment.packSize();
    }

    try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(CREATE, APPEND, SYNC))) {
      ByteBuffer logBuffer = ByteBuffer.allocate(bufferSize);

      for (TransactionSegment segment : segments) {
        segment.pack(logBuffer);
      }
      logBuffer.flip();

      logFileChannel.write(logBuffer);
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }

    return this;
  }

  public TransactionLog setLogPath(Path logPath) {
    Objects.requireNonNull(logPath);

    this.logPath = logPath;
    return this;
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
