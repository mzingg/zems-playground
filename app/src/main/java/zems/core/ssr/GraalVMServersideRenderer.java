package zems.core.ssr;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import zems.core.contentbus.ContentBus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

public class GraalVMServersideRenderer implements ServerSideRenderer {

  private static final String LANGUAGE_JS = "js";
  private static final String SPECIAL_VARIABLE_CONTENT_BUS = "ContentBusService";
  private static final String SUFFIX_JS_MODULE = ".ssr.mjs";
  private static final String PROP_RESOURCE_TYPE = "resourceType";
  private static final String PROP_PATH = "path";
  private static final String METHOD_THEN = "then";
  private static final String METHOD_CATCH = "catch";
  private static final String PREFIX_CANVAS_PATH = "app/src/main/canvas";

  private final ContentBus contentBus;

  public GraalVMServersideRenderer(ContentBus contentBus) {
    this.contentBus = contentBus;
  }

  @Override
  public String render(String renderType, String entryResourceType, String contentPath) throws IOException {
    // TODO: load ssr variant of Render and put in rendered JS
    return renderJavascript(renderType, entryResourceType, contentPath);
  }

  private String renderJavascript(String renderType, String entryResourceType, String contentPath) throws IOException {
    try (Context context = Context.newBuilder(LANGUAGE_JS)
        .allowExperimentalOptions(true)
        .allowHostAccess(HostAccess.ALL)
        .allowIO(true)
        .build()) {

      context.getBindings(LANGUAGE_JS)
          .putMember(SPECIAL_VARIABLE_CONTENT_BUS, contentBus);

      String renderName = renderType.substring(renderType.lastIndexOf('/'));
      String fileName = renderName + SUFFIX_JS_MODULE;
      String scriptResourcePath = String.format("%s/%s/%s", PREFIX_CANVAS_PATH, renderType, fileName);
      Path scriptPath = Paths.get(scriptResourcePath);
      Source jsSource = Source.newBuilder(LANGUAGE_JS, Files.readString(scriptPath), fileName)
          .build();

      final String[] result = {""};
      final Object[] errorState = {null};
      context.eval(jsSource)
          .execute(createJSObject(Map.of(PROP_RESOURCE_TYPE, entryResourceType, PROP_PATH, contentPath), context))
          .invokeMember(METHOD_THEN, (Consumer<Object>) (value) -> result[0] = value.toString())
          .invokeMember(METHOD_CATCH, (Consumer<Object>) (value) -> errorState[0] = value);

      if (errorState[0] != null) {
        throw new IOException(String.format("Error evaluating script [%s]: %s", scriptResourcePath, errorState[0]));
      }

      return result[0];
    }
  }

  private static Value createJSObject(final Map<Object, Object> map, Context context) {
    Value objectConstructor = context.getBindings("js").getMember("Object");
    Value object = objectConstructor.execute();
    for (Map.Entry<Object, Object> entry : map.entrySet()) {
      object.putMember(entry.getKey().toString(), entry.getValue());
    }
    return object;
  }

}
