package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;

import no.nav.common.leaderelection.LeaderElectionClient;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class PubliseringSchedulerConfig {

    @Bean
    public PubliseringAvRegistreringEventsScheduler publiseringAvRegistreringEventsScheduler(
            PubliseringAvEventsService publiseringAvEventsService,
            LeaderElectionClient leaderElectionClient
    ) {
        return new PubliseringAvRegistreringEventsScheduler(publiseringAvEventsService, leaderElectionClient);
    }

}
