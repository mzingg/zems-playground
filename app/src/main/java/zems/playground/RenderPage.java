package zems.playground;

import zems.core.contentbus.ContentBus;
import zems.core.contentbus.InMemoryContentBus;
import zems.core.ssr.GraalVMServersideRenderer;
import zems.core.ssr.ServerSideRenderer;

import java.io.IOException;

public class RenderPage {

  private static final ContentBus contentBus = new InMemoryContentBus();
  private static final ServerSideRenderer renderer = new GraalVMServersideRenderer(contentBus);

  public static void main(String[] args) throws IOException {

    System.out.println(renderer.render(
        "zems/playground/page",
        "zems/playground/App",
        "/content/playground/de/de"
    ));

  }
}
