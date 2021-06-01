package zems.core.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.Properties;
import zems.core.properties.InMemoryProperties;
import zems.core.properties.value.AnyValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ByteChannel;
import java.util.*;

public class InMemoryPersistenceProvider implements PersistenceProvider {

  private Map<String, Properties> content = new HashMap<>();

  @Override
  public Optional<Content> read(String path) {
    if (content.containsKey(path)) {
      return Optional.of(new Content(path, content.get(path)));
    }
    return Optional.empty();
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return Optional.empty();
  }

  public InMemoryPersistenceProvider withInitialState(String jsonResourcePath) {
    ClassPathResource resource = new ClassPathResource(jsonResourcePath);
    try (InputStream jsonStream = resource.getInputStream()) {
      Map<String, Map<String, Object>> complexInitialState = new ObjectMapper().readValue(jsonStream, new TypeReference<>() {
      });
      Map<String, Map<String, Object>> flattenedState = new HashMap<>();

      for (Map.Entry<String, Map<String, Object>> entry : complexInitialState.entrySet()) {
        flattenStateMap(entry.getKey(), entry.getValue(), flattenedState, 0);
      }

      content = toProperties(flattenedState);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return this;
  }

  private Map<String, Properties> toProperties(Map<String, Map<String, Object>> map) {
    HashMap<String, Properties> result = new LinkedHashMap<>();
    for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
      Properties properties = new InMemoryProperties();
      for (Map.Entry<String, Object> propEntry : entry.getValue().entrySet()) {
        properties.put(propEntry.getKey(), AnyValue.of(propEntry.getValue()));
      }
      result.put(entry.getKey(), properties);
    }
    return result;
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

}