package zems.playground.contentbus;

import java.util.List;
import java.util.Map;

public class InMemoryContentBus implements ContentBus {

  private static final Map<String, Object> PAGE_PROPERTIES = Map.of(
      "pageTitle", "A Page Title from the server",
      "contentParsys", Map.<String, Object>of("components", List.of(
          Map.of(
              "resourceType", "zems/playground/TextImage",
              "path", "/content/playground/de/de>contentParsys:one"
          ),
          Map.<String, Object>of(
              "resourceType", "zems/playground/Text",
              "path", "/content/playground/de/de>contentParsys:two"
          )
      ))
  );

  @Override
  public Object getProperties(String path) {
    Object props = Map.of();
    if (path.equals("/content/playground/de/de")) {
      props = PAGE_PROPERTIES;
    } else if (path.equals("/content/playground/de/de>contentParsys")) {
      props = PAGE_PROPERTIES.get("contentParsys");
    }
    return props;
  }

}
