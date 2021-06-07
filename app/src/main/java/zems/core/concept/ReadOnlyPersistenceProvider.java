package zems.core.concept;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public interface ReadOnlyPersistenceProvider extends AutoCloseable {

    Optional<Content> read(String path);

    Optional<ByteChannel> readBinary(String binaryId);

    @Override
    default void close() throws Exception {

    }
}
