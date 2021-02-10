package no.nav.fo.veilarbregistrering.config;

import no.nav.common.sts.NaisSystemUserTokenProvider;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayConfig;
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeGatewayConfig;
import no.nav.fo.veilarbregistrering.autorisasjon.PepConfig;
import no.nav.fo.veilarbregistrering.bruker.adapter.PersonGatewayConfig;
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.helsesjekk.HelsesjekkConfig;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig;
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig;
import no.nav.fo.veilarbregistrering.metrics.MetricsConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig;
import no.nav.fo.veilarbregistrering.registrering.publisering.scheduler.PubliseringSchedulerConfig;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import({
        FilterConfig.class,
        ServiceBeansConfig.class,
        DatabaseConfig.class,
        RepositoryConfig.class,
        KafkaConfig.class,
        PepConfig.class,
        Norg2GatewayConfig.class,
        CacheConfig.class,
        UnleashConfig.class,
        MetricsConfig.class,
        PersonGatewayConfig.class,
        ArbeidsforholdGatewayConfig.class,
        OppfolgingGatewayConfig.class,
        OppgaveGatewayConfig.class,
        SykemeldingGatewayConfig.class,
        PdlOppslagConfig.class,
        EnhetGatewayConfig.class,
        KrrConfig.class,
        FormidlingsgruppeGatewayConfig.class,
        PubliseringSchedulerConfig.class,
        HelsesjekkConfig.class,
        ObjectMapperConfig.class
})
@EnableScheduling
public class ApplicationConfig {

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return new NaisSystemUserTokenProvider(
                getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL"),
                getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"),
                getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD")
        );
    }

}
