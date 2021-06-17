package zems.playground.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zems.core.concept.PersistenceProvider;
import zems.core.contentbus.TransactionalContentBusConfigurer;
import zems.core.contentbus.persistence.FilePersistenceProvider;
import zems.core.contentbus.persistence.IndexedPersistenceProvider;
import zems.core.contentbus.persistence.LogBackedPersistenceProvider;
import zems.core.contentbus.transaction.AtomicSequenceGenerator;
import zems.core.contentbus.transaction.HotTransactionLog;
import zems.core.contentbus.transaction.InMemoryTransactionLogStatistics;
import zems.core.contentbus.transaction.MainTransactionLog;
import zems.core.utils.ZemsIoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class ContentBusConfiguration implements TransactionalContentBusConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(ContentBusConfiguration.class);

    @Bean
    @Override
    public PersistenceProvider<?> persistenceProvider() {
        return persistenceProvider(true, 30);
    }

    public PersistenceProvider<?> persistenceProvider(boolean withStatsMonitor, int commitScheduleIntervalInSeconds) {

        Path contentBusMainDirectory = Path.of("contentbus");
        Path contentDirectory = contentBusMainDirectory.resolve("content");
        Path binariesDirectory = contentBusMainDirectory.resolve("binaries");
        Path transactionDirectory = contentBusMainDirectory.resolve("log");
        Path mainLogPath = transactionDirectory.resolve("main.tn");
        Path mainLogRedPath = transactionDirectory.resolve("main.red.tn");
        Path mainLogGreenPath = transactionDirectory.resolve("main.green.tn");

        AtomicSequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();

        FilePersistenceProvider store = new FilePersistenceProvider(
          contentDirectory, binariesDirectory
        );
        if (!Files.exists(contentDirectory)) {
            try {
                ZemsIoUtils.createParentDirectories(contentDirectory);
                Files.createDirectory(contentDirectory);
                store.initFromJson("zems/core/ContentBus/initialState.json");
            } catch (IOException ioException) {
                throw new IllegalStateException(ioException);
            }
        }

        InMemoryTransactionLogStatistics stats = new InMemoryTransactionLogStatistics();
        if (withStatsMonitor) {
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> LOG.info(stats.toString()), 10, 10, TimeUnit.SECONDS);
        }

        MainTransactionLog log = new MainTransactionLog(commitScheduleIntervalInSeconds, stats)
          .setLogPath(mainLogPath)
          .setSequenceGenerator(sequenceGenerator)
          .setStore(store);

        HotTransactionLog hotLog = new HotTransactionLog(commitScheduleIntervalInSeconds, stats)
          .setSequenceGenerator(sequenceGenerator)
          .setCommitLog(log)
          .setRedHeadPath(mainLogRedPath)
          .setGreenHeadPath(mainLogGreenPath);

        LogBackedPersistenceProvider logBackedStore = new LogBackedPersistenceProvider(store, hotLog);

        return new IndexedPersistenceProvider(
          logBackedStore,
          "maximumSize=10000,expireAfterWrite=30s"
        );
    }
}
