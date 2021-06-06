package zems.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.SequenceGenerator;
import zems.core.contentbus.TransactionalContentBusConfigurer;
import zems.core.contentbus.persistence.InMemoryPersistenceProvider;
import zems.core.contentbus.persistence.IndexedPersistenceProvider;
import zems.core.contentbus.transaction.AtomicSequenceGenerator;
import zems.core.contentbus.transaction.HotTransactionLog;
import zems.core.contentbus.transaction.MainTransactionLog;

@Configuration
public class ContentBusConfiguration implements TransactionalContentBusConfigurer {

  @Bean
  @Override
  public PersistenceProvider<?> persistenceProvider() {
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    PersistenceProvider<InMemoryPersistenceProvider> persistenceProviderStore =
        new InMemoryPersistenceProvider().loadFromClassPath("zems/core/ContentBus/initialState.json");

    IndexedPersistenceProvider persistenceProvider = new IndexedPersistenceProvider(
        persistenceProviderStore,
        "maximumSize=10000,expireAfterWrite=30s"
    );

    MainTransactionLog log = new MainTransactionLog();

    HotTransactionLog hotLog = new HotTransactionLog()
        .setSequenceGenerator(sequenceGenerator)
        .setCommitLog(log);

    return persistenceProviderStore;
  }
}
