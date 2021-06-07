package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;

import java.nio.channels.ByteChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class IndexedPersistenceProvider implements PersistenceProvider<IndexedPersistenceProvider> {

    private final InMemoryIndexBasedPersistenceProvider index;
    private final PersistenceProvider<?> store;

    public IndexedPersistenceProvider(PersistenceProvider<?> store, String indexSpec) {
        this.store = store;
        this.index = new InMemoryIndexBasedPersistenceProvider(indexSpec);
    }

    @Override
    public Optional<Content> read(String path) {
        Objects.requireNonNull(path);

        return index.read(path).or(() -> {
            Optional<Content> data = store.read(path);
            data.ifPresent(c -> index.update(Stream.of(c)));
            return data;
        });
    }

    @Override
    public Optional<ByteChannel> readBinary(String binaryId) {
        Objects.requireNonNull(binaryId);

        return index.readBinary(binaryId).or(() -> store.readBinary(binaryId));
    }

    @Override
    public IndexedPersistenceProvider write(Content content) {
        Objects.requireNonNull(content);

        store.write(content);
        index.update(Stream.of(content));
        return this;
    }

    @Override
    public void close() throws Exception {
        store.close();
    }
}
