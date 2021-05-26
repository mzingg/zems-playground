package zems.core.contentbus;

import java.util.List;
import java.util.Map;

public class InMemoryContentBus implements ContentBus {

  private static final Map<String, Map<String, Object>> CONTENT = Map.of(
      "/content/playground/de/de", Map.of(
          "pageTitle", "A Page Title from the server"),
      "/content/playground/de/de>contentParsys", Map.of("components", List.of(
          Map.of(
              "resourceType", "zems/playground/TextImage",
              "path", "/content/playground/de/de>contentParsys:textimage1"),
          Map.<String, Object>of(
              "resourceType", "zems/playground/Text",
              "path", "/content/playground/de/de>contentParsys:text1"),
          Map.<String, Object>of(
              "resourceType", "zems/playground/Text",
              "path", "/content/playground/de/de>contentParsys:text2"))),
      "/content/playground/de/de>contentParsys:textimage1", Map.of(
          "text", "A TextImage component from the server",
          "imageSrc", "data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7"),
      "/content/playground/de/de>contentParsys:text1", Map.of(
          "text", "The first Text component from the server"),
      "/content/playground/de/de>contentParsys:text2", Map.of(
          "text", "The second Text component from the server")
  );

  @Override
  public Object getProperties(String path) {
    Object props = Map.of();
    if (CONTENT.containsKey(path)) {
      props = CONTENT.get(path);
    }
    return props;
  }

}
