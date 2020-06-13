package no.nav.fo.veilarbregistrering.registrering.scheduler;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import no.nav.fo.veilarbregistrering.registrering.bruker.OppgaveForAvvistRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
@EnableScheduling
public class OppgaveForAvvistRegistreringSchedulerConfig {

    @Bean
    public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder
                .withLockProvider(lockProvider)
                .withPoolSize(10)
                .withDefaultLockAtMostFor(Duration.ofMinutes(10))
                .build();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

    @Bean
    public OppgaveForAvvistRegistreringScheduler oppgaveForAvvistRegistreringScheduler(
            OppgaveForAvvistRegistreringService oppgaveForAvvistRegistreringService,
            UnleashService unleashService) {
        return new OppgaveForAvvistRegistreringScheduler(
                oppgaveForAvvistRegistreringService, unleashService);
    }
}
