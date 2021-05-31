package zems.core.contentbus.impl;

import zems.core.contentbus.Content;
import zems.core.contentbus.ContentBus;
import zems.core.transaction.HotTransactionLog;
import zems.core.transaction.SequenceGenerator;

import java.util.Optional;

public class TransactionalContentBus implements ContentBus {

  private HotTransactionLog transactionLog = new HotTransactionLog(new SequenceGenerator());

  @Override
  public Optional<Content> read(String path) {
    return Optional.empty();
  }

  @Override
  public void write(Content content) {
    transactionLog.append(content);
  }

}
