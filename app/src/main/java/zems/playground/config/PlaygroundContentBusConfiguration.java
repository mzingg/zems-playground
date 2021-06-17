package zems.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import zems.config.AbstractContentBusConfiguration;
import zems.core.concept.PersistenceProvider;

@Configuration
public class PlaygroundContentBusConfiguration extends AbstractContentBusConfiguration {

    public PlaygroundContentBusConfiguration() {
        this(true);
    }

    public PlaygroundContentBusConfiguration(boolean statsMonitorEnabled) {
        super(statsMonitorEnabled);
    }

    @Override
    public int mainCommitScheduleIntervalInSeconds() {
        return 30; // 30s
    }

    @Override
    public int hotCommitScheduleIntervalInSeconds() {
        return 5; // 5s
    }

    @Bean
    @Override
    public PersistenceProvider<?> persistenceProvider() {
        return super.persistenceProvider();
    }

    @Bean("contentBusUrl")
    @Profile("localdev")
    public String contentBusUrlLocalDev() {
        return "http://localhost:8080/zems-contentbus";
    }

    @Bean("contentBusUrl")
    @Profile("prod")
    public String contentBusUrlProd() {
        return "https://playground.zems.io/zems-contentbus";
    }
}
