package zems.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.stereotype.Component;
import zems.core.concept.Properties;
import zems.core.properties.value.ValueJsonSupport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Component
public class ZemsJsonUtils {

  @Autowired
  private ObjectMapper objectMapper;

  public ZemsJsonUtils withOwnObjectMapper() {
    // make use of a Spring BeanFactory but it still can be used outside
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerSingleton("zemsMapper", new ValueJsonSupport());

    final JsonComponentModule jsonComponentModule = new JsonComponentModule();
    jsonComponentModule.setBeanFactory(beanFactory);
    jsonComponentModule.registerJsonComponents();

    this.objectMapper = new ObjectMapper().registerModule(jsonComponentModule);
    return this;
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
}
