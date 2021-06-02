package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public class IndexedPersistenceProvider implements PersistenceProvider {

  private PersistenceProvider index;
  private PersistenceProvider store;

  @Override
  public Optional<Content> read(String path) {
    return index.read(path).or(() -> store.read(path));
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return index.readBinary(contentId).or(() -> store.readBinary(contentId));
  }

}
