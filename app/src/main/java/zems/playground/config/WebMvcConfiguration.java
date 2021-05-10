package zems.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import zems.playground.handler.ZemsRequestHandler;

import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Bean
  public RouterFunction<?> routeDefinition() {
    return route()
        .GET(accept(MediaType.TEXT_HTML), ZemsRequestHandler::handleHtml)
        .resources(ZemsRequestHandler::resourceLookup)
        .build();
  }

}
