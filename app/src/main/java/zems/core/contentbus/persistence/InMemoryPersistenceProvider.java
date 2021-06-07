package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.Properties;
import zems.core.utils.ZemsJsonUtils;

import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryPersistenceProvider implements PersistenceProvider<InMemoryPersistenceProvider> {

    private final Map<String, Properties> contentStore = new HashMap<>();

    @Override
    public Optional<Content> read(String path) {
        Objects.requireNonNull(path);

        if (contentStore.containsKey(path)) {
            return Optional.of(new Content(path, contentStore.get(path)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ByteChannel> readBinary(String binaryId) {
        return Optional.empty();
    }

    @Override
    public InMemoryPersistenceProvider write(Content content) {
        Properties properties = content.properties();
        if (contentStore.containsKey(content.path())) {
            contentStore.get(content.path()).modifyFrom(properties);
        } else {
            contentStore.put(content.path(), properties);
        }
        return this;
    }

    public InMemoryPersistenceProvider loadFromClassPath(String jsonResourcePath) {
        Objects.requireNonNull(jsonResourcePath);

        contentStore.clear();
        contentStore.putAll(new ZemsJsonUtils().loadAndFlatten(jsonResourcePath));

        return this;
    }

}