package zems.playground.ssr;

import zems.playground.contentbus.ContentBus;

import java.io.IOException;

public interface ServerSideRenderer {

  String render(String renderType, String entryResourceType, String contentPath, ContentBus contentBus) throws IOException;

}
