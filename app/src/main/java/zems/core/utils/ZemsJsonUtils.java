package zems.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import zems.core.concept.Properties;
import zems.core.properties.InMemoryProperties;
import zems.core.properties.value.ReferenceValue;
import zems.core.properties.value.ValueJsonSupport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

@Component
public class ZemsJsonUtils {

    private final ObjectMapper objectMapper;

    public ZemsJsonUtils() {
        // make use of a Spring BeanFactory but it still can be used outside
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("zemsMapper", new ValueJsonSupport());

        final JsonComponentModule jsonComponentModule = new JsonComponentModule();
        jsonComponentModule.setBeanFactory(beanFactory);
        jsonComponentModule.registerJsonComponents();

        this.objectMapper = new ObjectMapper().registerModule(jsonComponentModule);
    }

    public ZemsJsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String asJsonString(Object value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(objectMapper);

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Properties fromPath(Path path) {
        try {
            return objectMapper.readValue(path.toFile(), Properties.class);
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    public Map<String, Properties> loadAndFlatten(String jsonResourcePath) {
        Objects.requireNonNull(jsonResourcePath);

        ClassPathResource resource = new ClassPathResource(jsonResourcePath);
        try (InputStream jsonStream = resource.getInputStream()) {
            return loadAndFlatten(jsonStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<String, Properties> loadAndFlatten(InputStream jsonStream) {
        Objects.requireNonNull(jsonStream);

        try {
            Map<String, Map<String, Object>> jsonTree = objectMapper.readValue(
              jsonStream, new TypeReference<>() {}
            );
            Map<String, Properties> result = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : jsonTree.entrySet()) {
                flatten(entry.getKey(), entry.getValue(), result, 0);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void flatten(String currentPath, Map<String, Object> currentObject, Map<String, Properties> result, int level) {
        String pathSuffix = level == 0 ? ">" : ":";
        Properties properties = new InMemoryProperties();
        properties.put("path", currentPath);
        for (Map.Entry<String, Object> entry : currentObject.entrySet()) {
            if (entry.getValue() instanceof Map) {
                String referencePath = currentPath + pathSuffix + entry.getKey();
                properties.putValue(entry.getKey(), new ReferenceValue(referencePath));
                flatten(referencePath, (Map<String, Object>) entry.getValue(), result, level + 1);
            } else if (entry.getValue() instanceof List) {
                ArrayList<Map<String, Object>> valueList = (ArrayList<Map<String, Object>>) entry.getValue();
                ArrayList<ReferenceValue> referenceList = new ArrayList<>();
                int counter = 1;
                for (Map<String, Object> listState : valueList) {
                    String referencePath = currentPath + pathSuffix + entry.getKey() + counter;
                    flatten(referencePath, listState, result, level + 1);
                    referenceList.add(new ReferenceValue(referencePath));
                    counter++;
                }
                properties.put(entry.getKey(), referenceList);
            } else {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        result.put(currentPath, properties);
    }

}
