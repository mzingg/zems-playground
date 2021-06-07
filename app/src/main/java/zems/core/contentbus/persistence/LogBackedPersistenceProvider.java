package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.contentbus.transaction.HotTransactionLog;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public class LogBackedPersistenceProvider implements PersistenceProvider<LogBackedPersistenceProvider>, AutoCloseable {

    private final PersistenceProvider<?> store;
    private HotTransactionLog hotLog;

    public LogBackedPersistenceProvider(PersistenceProvider<?> store, HotTransactionLog hotLog) {
        this.store = store;
        this.hotLog = hotLog;

        hotLog.open();
    }

    @Override
    public LogBackedPersistenceProvider write(Content content) {
        hotLog.append(content);
        return this;
    }

    @Override
    public Optional<Content> read(String path) {
        return store.read(path);
    }

    @Override
    public Optional<ByteChannel> readBinary(String binaryId) {
        return store.readBinary(binaryId);
    }

    @Override
    public void close() throws Exception {
        hotLog.close();
    }
}
