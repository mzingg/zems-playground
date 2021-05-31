package zems.playground;

import zems.core.contentbus.ContentBusSimple;
import zems.core.contentbus.InMemoryContentBusSimple;
import zems.core.ssr.GraalVMServersideRenderer;
import zems.core.ssr.ServerSideRenderer;

import java.io.IOException;

public class RenderPage {

  private static final ContentBusSimple CONTENT_BUS_SIMPLE = new InMemoryContentBusSimple()
      .withInitialState("zems/core/ContentBus/initialState.json");
  private static final ServerSideRenderer renderer = new GraalVMServersideRenderer(CONTENT_BUS_SIMPLE);

  public static void main(String[] args) throws IOException {

    System.out.println(renderer.render(
        "zems/playground/page",
        "zems/playground/App",
        "/content/playground/de/de"
    ));

  }
}
