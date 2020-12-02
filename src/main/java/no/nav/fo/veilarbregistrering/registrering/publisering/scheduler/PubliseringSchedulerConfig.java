package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;

import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class PubliseringSchedulerConfig {

    @Bean
    public PubliseringAvRegistreringEventsScheduler publiseringAvRegistreringEventsScheduler(
            PubliseringAvEventsService publiseringAvEventsService
    ) {
        return new PubliseringAvRegistreringEventsScheduler(publiseringAvEventsService);
    }

}
