package zems.playground;

import zems.core.concept.ContentBus;
import zems.core.contentbus.TransactionalContentBus;
import zems.core.ssr.GraalVMServersideRenderer;
import zems.core.ssr.ServerSideRenderer;
import zems.playground.config.ContentBusConfiguration;

import java.io.IOException;

public class RenderPage {

    private static final ContentBus contentBus = new TransactionalContentBus(new ContentBusConfiguration().persistenceProvider());
    private static final ServerSideRenderer renderer = new GraalVMServersideRenderer(contentBus);

    public static void main(String[] args) throws IOException {

        System.out.println(renderer.render(
          "zems/playground/page",
          "zems/playground/App",
          "/content/playground/de/de"
        ));

        System.exit(0);
    }
}
