package zems.playground.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import zems.config.AbstractZemsWebSocketConfiguration;

@Configuration
@EnableWebSocketMessageBroker
public class PlaygroundWebSocketConfiguration extends AbstractZemsWebSocketConfiguration {

}
