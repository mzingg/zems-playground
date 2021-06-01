package zems.core.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public class IndexedPersistenceProvider implements PersistenceProvider {

  @Override
  public Optional<Content> read(String path) {
    return Optional.empty();
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return Optional.empty();
  }

}
