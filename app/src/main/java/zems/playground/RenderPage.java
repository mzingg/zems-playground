package zems.playground;

import zems.core.concept.ContentBus;
import zems.core.concept.ServerSideRenderRequest;
import zems.core.concept.ServerSideRenderer;
import zems.core.contentbus.TransactionalContentBus;
import zems.core.ssr.Canvas;
import zems.core.ssr.GraalVMServersideRenderer;
import zems.playground.config.PlaygroundContentBusConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RenderPage {

    private static final ContentBus contentBus = new TransactionalContentBus(
      new PlaygroundContentBusConfiguration(false).persistenceProvider()
    );
    private static final ServerSideRenderer renderer = new GraalVMServersideRenderer(contentBus);

    public static void main(String[] args) throws IOException {

        List<String> supportedComponents = Arrays.asList(
          "zems/playground/App",
          "zems/playground/PageTitle",
          "zems/core/Container",
          "zems/playground/Text",
          "zems/playground/Image",
          "zems/playground/TextImage"
        );

        ServerSideRenderRequest request = new ServerSideRenderRequest(
          new Canvas("zems/playground/page"),
          "/content/playground/de/de",
          Locale.forLanguageTag("de-CH"),
          "ch",
          supportedComponents,
          System.out::println,
          (script, error) -> System.err.printf("In script %s: %s%n", script, error)
        );

        renderer.render(request);

        System.exit(0);
    }
}
