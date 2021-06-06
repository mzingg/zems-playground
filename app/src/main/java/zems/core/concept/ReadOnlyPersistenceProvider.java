package zems.core.concept;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public interface ReadOnlyPersistenceProvider {

  Optional<Content> read(String path);

  Optional<ByteChannel> readBinary(String binaryId);

}
