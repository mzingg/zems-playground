package zems.core.ssr;

import java.io.IOException;

public interface ServerSideRenderer {

  String render(String renderType, String entryResourceType, String contentPath) throws IOException;

}
