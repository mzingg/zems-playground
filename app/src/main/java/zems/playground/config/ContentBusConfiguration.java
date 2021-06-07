package zems.playground.config;

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

import java.nio.file.Path;

@Configuration
public class ContentBusConfiguration implements TransactionalContentBusConfigurer {

    @Bean
    @Override
    public PersistenceProvider<?> persistenceProvider() {
        Path contentBusMainDirectory = Path.of("contentbus");
        Path contentDirectory = contentBusMainDirectory.resolve("content");
        Path binariesDirectory = contentBusMainDirectory.resolve("binaries");
        Path transactionDirectory = contentBusMainDirectory.resolve("log");
        Path mainLogPath = transactionDirectory.resolve("main.tn");
        Path mainLogRedPath = transactionDirectory.resolve("main.red.tn");
        Path mainLogGreenPath = transactionDirectory.resolve("main.green.tn");

        AtomicSequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();

        PersistenceProvider<?> store = new FilePersistenceProvider(
          contentDirectory, binariesDirectory
        );

        InMemoryTransactionLogStatistics stats = new InMemoryTransactionLogStatistics();

        MainTransactionLog log = new MainTransactionLog(30, stats)
          .setLogPath(mainLogPath)
          .setSequenceGenerator(sequenceGenerator)
          .setStore(store);

        HotTransactionLog hotLog = new HotTransactionLog()
          .setSequenceGenerator(sequenceGenerator)
          .setCommitLog(log)
          .setRedHeadPath(mainLogRedPath)
          .setGreenHeadPath(mainLogGreenPath);

        LogBackedPersistenceProvider logBackedStore = new LogBackedPersistenceProvider(
          store, hotLog
        );

        return new IndexedPersistenceProvider(
          logBackedStore,
          "maximumSize=10000,expireAfterWrite=30s"
        );
    }
}
