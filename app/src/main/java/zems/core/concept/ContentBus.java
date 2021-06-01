package zems.core.concept;

import java.nio.channels.ByteChannel;
import java.util.Optional;

public interface ContentBus {

  Optional<Content> read(String path);

  Optional<ByteChannel> readBinary(String contentId);

  void write(Content content);

}
