package zems.core.contentbus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryContentBusSimple implements ContentBusSimple {

  private Map<String, Map<String, Object>> content = new HashMap<>();

  public InMemoryContentBusSimple withInitialState(String jsonResourcePath) {
    ClassPathResource resource = new ClassPathResource(jsonResourcePath);
    try (InputStream jsonStream = resource.getInputStream()) {
      Map<String, Map<String, Object>> complexInitialState = new ObjectMapper().readValue(jsonStream, new TypeReference<>() {
      });
      Map<String, Map<String, Object>> flattenedState = new HashMap<>();

      for (Map.Entry<String, Map<String, Object>> entry : complexInitialState.entrySet()) {
        flattenStateMap(entry.getKey(), entry.getValue(), flattenedState, 0);
      }

      content = flattenedState;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return this;
  }

  @SuppressWarnings("unchecked")
  private void flattenStateMap(String currentPath, Map<String, Object> currentState, Map<String, Map<String, Object>> flattenedTarget, int level) {
    String pathSuffix = level == 0 ? ">" : ":";
    Map<String, Object> props = new HashMap<>();
    props.put("path", currentPath);
    for (Map.Entry<String, Object> entry : currentState.entrySet()) {
      if (entry.getValue() instanceof Map) {
        String referencePath = currentPath + pathSuffix + entry.getKey();
        String resourceType = (String) ((Map<String, Object>) entry.getValue()).getOrDefault("resourceType", "zems/core/Any");

        props.put(entry.getKey(), Map.of("resourceType", resourceType, "loadFrom", referencePath, "path", referencePath));

        flattenStateMap(referencePath, (Map<String, Object>) entry.getValue(), flattenedTarget, level + 1);
      } else if (entry.getValue() instanceof ArrayList) {
        ArrayList<Map<String, Object>> valueList = (ArrayList<Map<String, Object>>) entry.getValue();
        ArrayList<Map<String, Object>> referenceList = new ArrayList<>();
        int counter = 1;
        for (Map<String, Object> listState : valueList) {
          String referencePath = currentPath + pathSuffix + entry.getKey() + counter;
          String resourceType = (String) listState.getOrDefault("resourceType", "zems/core/Any");
          flattenStateMap(referencePath, listState, flattenedTarget, level + 1);
          referenceList.add(Map.of("resourceType", resourceType, "loadFrom", referencePath, "path", referencePath));
          counter++;
        }
        props.put(entry.getKey(), referenceList);
      } else {
        props.put(entry.getKey(), entry.getValue());
      }
    }
    flattenedTarget.put(currentPath, props);
  }

  @Override
  public Object getProperties(String path) {
    Object props = Map.of();
    if (content.containsKey(path)) {
      props = content.get(path);
    }
    return props;
  }

}
