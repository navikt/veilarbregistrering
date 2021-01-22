package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class PubliseringSchedulerConfig {

    @Bean
    public PubliseringAvRegistreringEventsScheduler publiseringAvRegistreringEventsScheduler() {
        return mock(PubliseringAvRegistreringEventsScheduler.class);
    }

}
