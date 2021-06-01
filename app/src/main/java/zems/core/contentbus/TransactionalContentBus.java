package zems.core.contentbus;

import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.SequenceGenerator;
import zems.core.persistence.InMemoryPersistenceProvider;
import zems.core.transaction.AtomicSequenceGenerator;
import zems.core.transaction.HotTransactionLog;
import zems.core.transaction.MainTransactionLog;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public class TransactionalContentBus implements ContentBus {

  private SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
  private PersistenceProvider persistenceProvider = new InMemoryPersistenceProvider()
      .withInitialState("zems/core/ContentBus/initialState.json");

  private MainTransactionLog log = new MainTransactionLog();

  private HotTransactionLog hotLog = new HotTransactionLog()
      .setSequenceGenerator(sequenceGenerator)
      .setCommitLog(log);


  @Override
  public Optional<Content> read(String path) {
    return persistenceProvider.read(path);
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return persistenceProvider.readBinary(contentId);
  }

  @Override
  public void write(Content content) {
    hotLog.append(content);
  }

}
