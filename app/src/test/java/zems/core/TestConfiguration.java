package zems.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zems.core.utils.ZemsJsonUtils;

@Configuration
public class TestConfiguration {

  private ZemsJsonUtils jsonUtils = new ZemsJsonUtils();

  @Bean
  public ZemsJsonUtils getJsonUtils() {
    return jsonUtils;
  }
}
