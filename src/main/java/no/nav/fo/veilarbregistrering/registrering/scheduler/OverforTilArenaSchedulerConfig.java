package no.nav.fo.veilarbregistrering.registrering.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class OverforTilArenaSchedulerConfig {

    @Bean
    public OverforTilArenaTask overforTilArenaTask() {
        return new OverforTilArenaTask();
    }
}
