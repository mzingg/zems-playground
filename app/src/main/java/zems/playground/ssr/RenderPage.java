package zems.playground.ssr;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

public class RenderPage {

  private static final String LANGUAGE_JS = "js";

  public static void main(String[] args) throws IOException {
    Path scriptPath = Paths.get("app/src/main/renders/zems/playground/page/page.ssr.mjs");

    try (Context context = Context.newBuilder(LANGUAGE_JS)
        .allowExperimentalOptions(true)
        .allowHostAccess(HostAccess.ALL)
        .allowIO(true)
        .option("js.strict", "true")
        .option("js.foreign-object-prototype", "true")
        .build()) {

      Source jsSource = Source.newBuilder(LANGUAGE_JS, Files.readString(scriptPath), "page.ssr.mjs")
          .build();

      String rootResourceType = "zems/playground/App";
      String currentPath = "/content/playground/de/de";

      context.eval(jsSource)
          .execute(createJSObject(Map.of("resourceType", rootResourceType, "path", currentPath), context))
          .invokeMember("then", (Consumer<Object>) System.out::println)
          .invokeMember("catch", (Consumer<Object>) (value) -> System.out.println("Promise failed!" + value));
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
