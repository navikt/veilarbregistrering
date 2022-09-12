package no.nav.fo.veilarbregistrering.config

import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.utils.AzureAdServiceTokenProviderBuilder
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayConfig
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeGatewayConfig
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonConfig
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig
import no.nav.fo.veilarbregistrering.config.filters.FilterConfig
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig
import no.nav.fo.veilarbregistrering.featuretoggle.UnleashConfig
import no.nav.fo.veilarbregistrering.helsesjekk.HelsesjekkConfig
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.MetricsConfig
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig
import no.nav.fo.veilarbregistrering.registrering.publisering.scheduler.PubliseringSchedulerConfig
import no.nav.fo.veilarbregistrering.tokenveksling.TokenExchangeConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.UnsupportedMediaTypeStatusException


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
    FormidlingsgruppeGatewayConfig::class,
    PubliseringSchedulerConfig::class,
    HelsesjekkConfig::class,
    ObjectMapperConfig::class,
    SwaggerConfig::class,
    TokenExchangeConfig::class
)
@EnableScheduling
class ApplicationConfig {
    
    @Bean
    fun serviceToServiceTokenProvider(): ServiceToServiceTokenProvider {
        return AzureAdServiceTokenProviderBuilder.builder()
            .withEnvironmentDefaults()
            .build()
    }

    @ControllerAdvice
    class ControllerConfig {
        @ExceptionHandler
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        fun handle(e: HttpMessageNotReadableException?) {
            logger.warn("Returning HTTP 400 Bad Request", e)
            throw e!!
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        fun handle(e: UnsupportedMediaTypeStatusException) {
            logger.warn("Returning HTTP 415 Unsupported Media Type", e)
            throw e!!
        }
    }
}