package zems.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import zems.core.handler.ZemsRequestHandler;

import static org.springframework.web.servlet.function.RequestPredicates.pathExtension;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public RouterFunction<?> routeDefinition() {
        return route()
          .GET(pathExtension("html"), ZemsRequestHandler::handleHtml)
          .GET(pathExtension("mjs"), ZemsRequestHandler::handleJavascriptModule)
          .build();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
          .mediaType("mjs", MediaType.valueOf("application/javascript"));
    }
}
