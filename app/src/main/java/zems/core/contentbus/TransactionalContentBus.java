package zems.core.contentbus;

import org.springframework.stereotype.Component;
import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.concept.PersistenceProvider;

import java.nio.channels.ByteChannel;
import java.util.Objects;
import java.util.Optional;

@Component
public class TransactionalContentBus implements ContentBus {

  private final PersistenceProvider<?> persistenceProvider;

  public TransactionalContentBus(PersistenceProvider<?> persistenceProvider) {
    this.persistenceProvider = persistenceProvider;
  }

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

    persistenceProvider.write(content);
  }

}
