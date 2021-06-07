package zems.core.contentbus.transaction;

import zems.core.concept.*;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.*;

public class MainTransactionLog implements TransactionLog<MainTransactionLog> {

    private static final int DEFAULT_READ_BUFFER_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MINIMAL_READ_BUFFER_SIZE = 256;

    private static final Consumer<Long> NO_POSITION_ACTION = lastPosition -> {
    };
    private static final Consumer<TransactionSegment> NO_SEGMENT_ACTION = segment -> {
    };
    private final ReentrantLock writeLock = new ReentrantLock();
    private final ReentrantLock commitLock = new ReentrantLock();
    private final boolean schedulerEnabled;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final TransactionLogStatistics stats;
    private SequenceGenerator sequenceGenerator;
    private Path logPath;
    private int readBufferSize;
    private boolean allowsSuperfluousData;
    private PersistenceProvider<?> store;

    public MainTransactionLog() {
        this(0, new TransactionLogStatistics() {});
    }

    public MainTransactionLog(int commitScheduleIntervalInSeconds, TransactionLogStatistics stats) {
        Objects.requireNonNull(stats);

        this.stats = stats.reset();
        this.readBufferSize = DEFAULT_READ_BUFFER_SIZE;
        this.allowsSuperfluousData = false;
        this.schedulerEnabled = commitScheduleIntervalInSeconds > 0;
        if (schedulerEnabled) {
            executor.scheduleWithFixedDelay(this::commit, commitScheduleIntervalInSeconds, commitScheduleIntervalInSeconds, TimeUnit.SECONDS);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void close() {
        writeLock.lock();
        commitLock.lock();
        try {
            stats.reset();
            if (schedulerEnabled) {
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            executor.shutdownNow();

            commitLock.unlock();
            writeLock.unlock();
        }
    }

    public long seekLastPosition() {
        long[] result = new long[]{0L};
        internalRead(
          NO_SEGMENT_ACTION,
          lastPosition -> result[0] = lastPosition
        );
        return result[0];
    }

    public Stream<Content> read() {
        Stream.Builder<Content> builder = Stream.builder();
        internalRead(
          segment -> builder.accept(new Content(segment.getPath(), segment.getData())),
          NO_POSITION_ACTION
        );
        return builder.build();
    }

    @Override
    public MainTransactionLog append(Content... content) {
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

    public MainTransactionLog append(TransactionSegment... segments) {
        Objects.requireNonNull(segments);
        ensureReady();

        int bufferSize = 0;
        for (TransactionSegment segment : segments) {
            bufferSize += segment.packSize();
        }
        bufferSize += segments.length; // separator bytes

        // we open (and close) the file for each append so that we are able to do a snapshot
        writeLock.lock();
        try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(CREATE, APPEND, SYNC))) {
            ByteBuffer logBuffer = ByteBuffer.allocate(bufferSize);

            for (TransactionSegment segment : segments) {
                logBuffer.put(ZemsIoUtils.RECORD_SEPARATOR_BYTE);
                segment.pack(logBuffer);
            }
            logBuffer.flip();

            logFileChannel.write(logBuffer);
            stats.countAppend().countAppendSegments(segments.length).countAppendAmountInBytes(bufferSize);
        } catch (IOException ioException) {
            stats.countAppendError();
            throw new IllegalStateException(ioException);
        } finally {
            writeLock.unlock();
        }

        return this;
    }

    public void commit() {
        if (store == null) {
            return;
        }
        // acquire a lock, so that only one commit action is performed at any given time (and threads)
        commitLock.lock();
        try {
            stats.countCommitStarted();
            Path snapshotPath = ZemsIoUtils.getSiblingWithNewExtension(
              logPath, ".snapshot.tn"
            );
            Path errorPath = ZemsIoUtils.getSiblingWithNewExtension(
              logPath, String.format(".error-%d.tn", System.currentTimeMillis())
            );

            // Make a snapshot of the current log.
            // This is a blocking operation but using move and touch should be fast on all OS.
            // This is also the reason we cannot keep the file handle open for this log.
            writeLock.lock();
            try {
                ZemsIoUtils.snapshot(logPath, snapshotPath);
            } finally {
                writeLock.unlock();
            }

            try (
              MainTransactionLog snapshotLog = new MainTransactionLog()
                .setSequenceGenerator(sequenceGenerator)
                .setLogPath(snapshotPath);
              MainTransactionLog errorLog = new MainTransactionLog()
                .setSequenceGenerator(sequenceGenerator)
                .setLogPath(errorPath)
            ) {

                // Now just read the snapshot log and call the write message for each content element.
                // If an error occurs we append the content to the error log.
                snapshotLog.read().forEach(content -> {
                    try {
                        store.write(content);
                        stats.countCommitContent();
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        errorLog.append(content);
                        stats.countCommitError();
                    }
                });
            }

            // cleanup snapshot file
            try {
                stats.countCommitAmountInBytes(Files.size(snapshotPath));
                Files.deleteIfExists(snapshotPath);
                // when no error occurred we can delete the error log as well
                if (Files.exists(errorPath) && Files.size(errorPath) == 0) {
                    Files.deleteIfExists(errorPath);
                }
            } catch (IOException e) {
                stats.countCommitFileCleanupError();
                throw new IllegalStateException(e);
            }

            stats.countCommitFinished();
        } finally {
            commitLock.unlock();
        }
    }

    @Override
    public MainTransactionLog setSequenceGenerator(SequenceGenerator sequenceGenerator) {
        Objects.requireNonNull(sequenceGenerator);

        this.sequenceGenerator = sequenceGenerator;
        return this;
    }

    public Path getLogPath() {
        ensureLogPathExistsAndIsWritable();

        return logPath;
    }

    public MainTransactionLog setLogPath(Path logPath) {
        Objects.requireNonNull(logPath);

        this.logPath = logPath;
        return this;
    }

    public MainTransactionLog setReadBufferSize(int readBufferSize) {
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
    public MainTransactionLog setAllowsSuperfluousData(boolean allowsSuperfluousData) {
        this.allowsSuperfluousData = allowsSuperfluousData;
        return this;
    }

    public MainTransactionLog setStore(PersistenceProvider<?> store) {
        Objects.requireNonNull(store);

        this.store = store;
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

    private void internalRead(Consumer<TransactionSegment> segmentConsumer, Consumer<Long> lastPositionConsumer) {
        ensureReady();

        try (FileChannel logFileChannel = FileChannel.open(logPath, Set.of(READ))) {

            long lastGoodPosition = 0;

            ByteBuffer buffer = ByteBuffer.allocate(readBufferSize);
            int bytesRead = logFileChannel.read(buffer);
            while (bytesRead > 0) {
                buffer.flip(); // set buffer to read mode

                boolean segmentTooLarge = false;
                boolean endReached = false;
                while (buffer.hasRemaining() && !segmentTooLarge && !endReached) {
                    buffer.mark(); // mark the beginning of this segment in case we exceed the readBuffer
                    byte separatorByte = buffer.get();
                    if (separatorByte == ZemsIoUtils.RECORD_SEPARATOR_BYTE) {
                        try {
                            segmentConsumer.accept(new TransactionSegment().unpack(buffer));
                            lastGoodPosition = logFileChannel.position();
                        } catch (BufferUnderflowException ignored) {
                            // our segment spans over the current read buffer
                            buffer.reset(); // reset to the last mark so that the remaining amount is correct
                            segmentTooLarge = true;
                        }
                    } else {
                        endReached = true;
                        if (!allowsSuperfluousData) {
                            throw new IllegalStateException("transaction log contains superfluous data");
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

            lastPositionConsumer.accept(lastGoodPosition);
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

}
