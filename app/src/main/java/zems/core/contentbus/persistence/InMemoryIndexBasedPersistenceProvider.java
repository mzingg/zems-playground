package zems.core.contentbus.persistence;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import zems.core.concept.Content;
import zems.core.concept.IndexBasedPersistenceProvider;

import java.nio.channels.ByteChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryIndexBasedPersistenceProvider implements IndexBasedPersistenceProvider {

    private final Cache<String, Content> index;

    public InMemoryIndexBasedPersistenceProvider(String cacheSpec) {
        Objects.requireNonNull(cacheSpec);

        index = CacheBuilder.from(cacheSpec).build();
    }

    @Override
    public void reset() {
        index.cleanUp();
    }

    @Override
    public void update(Stream<Content> contentStream) {
        Objects.requireNonNull(contentStream);

        contentStream.forEach(c -> index.put(c.path(), c));
    }

    @Override
    public Optional<Content> read(String path) {
        Objects.requireNonNull(path);

        return Optional.ofNullable(index.getIfPresent(path));
    }

    @Override
    public Optional<ByteChannel> readBinary(String binaryId) {
        // binary operations are not supported by this index
        return Optional.empty();
    }

}
