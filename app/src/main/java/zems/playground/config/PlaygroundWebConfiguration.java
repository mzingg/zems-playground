package zems.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import zems.config.AbstractZemsWebConfiguration;
import zems.core.contentbus.TransactionalContentBus;

@Configuration
@EnableWebMvc
public class PlaygroundWebConfiguration extends AbstractZemsWebConfiguration {

    public PlaygroundWebConfiguration(TransactionalContentBus contentBus) {
        super(contentBus);
    }

    @Override
    public String canvas() {
        return "zems/playground/page";
    }

    @Bean
    public RouterFunction<?> routeDefinition() {
        return routeBuilder().build();
    }

}
