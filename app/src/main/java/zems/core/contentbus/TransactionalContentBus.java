package zems.core.contentbus;

import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.transaction.AtomicSequenceGenerator;
import zems.core.transaction.HotTransactionLog;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public class TransactionalContentBus implements ContentBus {

  private HotTransactionLog transactionLog = new HotTransactionLog().setSequenceGenerator(new AtomicSequenceGenerator());

  private IndexedPersistenceProvider persistenceProvider = new IndexedPersistenceProvider();

  @Override
  public Optional<Content> read(String path) {
    return persistenceProvider.readIndexed(path);
  }

  @Override
  public void write(Content content) {
    transactionLog.append(content);
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return Optional.empty();
  }
}
