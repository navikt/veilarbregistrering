package no.nav.fo.veilarbregistrering.db;

import no.nav.common.abac.Pep;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.health.selftest.SelfTestChecks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;


@Configuration
public class HelsesjekkConfig {

    @Bean
    public SelfTestChecks selfTestChecks(JdbcTemplate jdbcTemplate,
                                         Pep veilarbPep,
                                         UnleashService unleashService) {
        List<SelfTestCheck> selfTestChecks = Arrays.asList(

                new SelfTestCheck("Enkel spørring mot Databasen til veilarregistrering.", true, checkDbHealth(jdbcTemplate)),
                new SelfTestCheck("ABAC tilgangskontroll - ping", true, veilarbPep.getAbacClient()),
                new SelfTestCheck("Sjekker at feature-toggles kan hentes fra Unleash", false, unleashService)
        );
        return new SelfTestChecks(selfTestChecks);
    }

    private HealthCheck checkDbHealth(JdbcTemplate jdbcTemplate) {
        return () -> {
            try {
                jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Long.class);
                return HealthCheckResult.healthy();
            } catch (Exception e) {
                return HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e);
            }
        };
    }
}
