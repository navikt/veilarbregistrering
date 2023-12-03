package no.nav.fo.veilarbregistrering.config

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheConfig
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayConfig
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler.ArbeidssokerperiodeSchedulerConfig
import no.nav.fo.veilarbregistrering.autentisering.AuthenticationConfig
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.TokenExchangeConfig
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonConfig
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.config.filters.FilterConfig
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig
import no.nav.fo.veilarbregistrering.featuretoggle.UnleashConfig
import no.nav.fo.veilarbregistrering.helsesjekk.HelsesjekkConfig
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig
import no.nav.fo.veilarbregistrering.metrics.MetricsConfig
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig
import no.nav.fo.veilarbregistrering.registrering.publisering.scheduler.PubliseringSchedulerConfig
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
    AutorisasjonConfig::class,
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
    PubliseringSchedulerConfig::class,
    HelsesjekkConfig::class,
    ObjectMapperConfig::class,
    SwaggerConfig::class,
    TokenExchangeConfig::class,
    AktorIdCacheConfig::class,
    ArbeidssokerperiodeSchedulerConfig::class,
)
@EnableScheduling
class ApplicationConfig