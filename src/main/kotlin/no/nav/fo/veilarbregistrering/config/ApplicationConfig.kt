package no.nav.fo.veilarbregistrering.config

import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayConfig
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeGatewayConfig
import no.nav.fo.veilarbregistrering.autorisasjon.GcpAutorisasjonConfig
import no.nav.fo.veilarbregistrering.autorisasjon.OnPremAutorisasjonConfig
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig
import no.nav.fo.veilarbregistrering.config.filters.FilterConfig
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig
import no.nav.fo.veilarbregistrering.featuretoggle.UnleashConfig
import no.nav.fo.veilarbregistrering.helsesjekk.HelsesjekkConfig
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig
import no.nav.fo.veilarbregistrering.metrics.MetricsConfig
import no.nav.fo.veilarbregistrering.migrering.konsument.adapter.MigrateClientConfig
import no.nav.fo.veilarbregistrering.migrering.konsument.scheduler.MigrateWorkerConfig
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig
import no.nav.fo.veilarbregistrering.registrering.publisering.scheduler.PubliseringSchedulerConfig
import no.nav.fo.veilarbregistrering.tokenveksling.TokenExchangeConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling


@Configuration
@Import(
    FilterConfig::class,
    ServiceBeansConfig::class,
    AuthenticationConfig::class,
    DatabaseConfig::class,
    RepositoryConfig::class,
    KafkaConfig::class,
    MigrateClientConfig::class,
    OnPremAutorisasjonConfig::class,
    GcpAutorisasjonConfig::class,
    Norg2GatewayConfig::class,
    CacheConfig::class,
    UnleashConfig::class,
    MetricsConfig::class,
    ArbeidsforholdGatewayConfig::class,
    OppfolgingGatewayConfig::class,
    OppgaveGatewayConfig::class,
    PdlOppslagConfig::class,
    EnhetGatewayConfig::class,
    KrrConfig::class,
    FormidlingsgruppeGatewayConfig::class,
    PubliseringSchedulerConfig::class,
    MigrateWorkerConfig::class,
    HelsesjekkConfig::class,
    ObjectMapperConfig::class,
    SwaggerConfig::class,
    TokenExchangeConfig::class
)
@EnableScheduling
class ApplicationConfig