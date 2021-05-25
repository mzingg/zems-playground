package zems.playground.ssr;

import zems.playground.contentbus.ContentBus;
import zems.playground.contentbus.InMemoryContentBus;

import java.io.IOException;

public class RenderPage {

  private static final ContentBus contentBus = new InMemoryContentBus();
  private static final ServerSideRenderer renderer = new GraalVMServersideRenderer();

  public static void main(String[] args) throws IOException {

    System.out.println(renderer.render(
        "zems/playground/page",
        "zems/playground/App",
        "/content/playground/de/de",
        contentBus
    ));

  }
}
