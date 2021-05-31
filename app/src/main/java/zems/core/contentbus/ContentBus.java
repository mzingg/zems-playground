package zems.core.contentbus;

import java.util.Optional;

public interface ContentBus {

  Optional<Content> read(String path);

  void write(Content content);

}
