package no.nav.fo.veilarbregistrering.config;

import no.nav.common.sts.NaisSystemUserTokenProvider;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayConfig;
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeGatewayConfig;
import no.nav.fo.veilarbregistrering.bruker.adapter.PersonGatewayConfig;
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.HelsesjekkConfig;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig;
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig;
import no.nav.fo.veilarbregistrering.registrering.publisering.scheduler.PubliseringSchedulerConfig;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig;
import no.nav.veilarboppfolging.config.FilterConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
        HelsesjekkConfig.class
})
@EnableScheduling
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {

    public static final String APPLICATION_NAME = "veilarbregistrering";

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return new NaisSystemUserTokenProvider(
                getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL"),
                getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"),
                getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD")
        );
    }


    /*@Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);

        InternalRegistreringStatusoversiktServlet internalRegistreringStatusoversiktServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalRegistreringStatusoversiktServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalRegistreringStatusoversiktServlet, InternalRegistreringStatusoversiktServlet.PATH);

        InternalRegistreringStatusServlet internalRegistreringStatusServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalRegistreringStatusServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalRegistreringStatusServlet, InternalRegistreringStatusServlet.PATH);

        InternalIdentServlet internalIdentServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalIdentServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalIdentServlet, InternalIdentServlet.PATH);

        InternalArbeidssokerServlet internalArbeidssokerServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalArbeidssokerServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalArbeidssokerServlet, InternalArbeidssokerServlet.PATH);
    }*/
}
