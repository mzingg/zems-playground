package zems.core.contentbus;

import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.concept.ReadOnlyPersistenceProvider;
import zems.core.concept.SequenceGenerator;
import zems.core.contentbus.persistence.InMemoryPersistenceProvider;
import zems.core.contentbus.transaction.AtomicSequenceGenerator;
import zems.core.contentbus.transaction.HotTransactionLog;
import zems.core.contentbus.transaction.MainTransactionLog;

import java.nio.channels.ByteChannel;
import java.util.Objects;
import java.util.Optional;

public class TransactionalContentBus implements ContentBus {

  private SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
  private ReadOnlyPersistenceProvider persistenceProvider = new InMemoryPersistenceProvider()
      .loadFromClassPath("zems/core/ContentBus/initialState.json");

  private MainTransactionLog log = new MainTransactionLog();

  private HotTransactionLog hotLog = new HotTransactionLog()
      .setSequenceGenerator(sequenceGenerator)
      .setCommitLog(log);


  @Override
  public Optional<Content> read(String path) {
    Objects.requireNonNull(path);

    return persistenceProvider.read(path);
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    Objects.requireNonNull(contentId);

    return persistenceProvider.readBinary(contentId);
  }

  @Override
  public void write(Content content) {
    Objects.requireNonNull(content);

    hotLog.append(content);
  }

}
