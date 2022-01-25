package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.instrument.Tag
import no.nav.common.auth.Constants
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events.*
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.HentOppfolgingStatusException
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerException
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil
import javax.ws.rs.core.HttpHeaders

open class OppfolgingClient(
    private val objectMapper: ObjectMapper,
    private val metricsService: PrometheusMetricsService,
    private val baseUrl: String,
    private val tokenProvider: () -> String,

    ) : AbstractOppfolgingClient(objectMapper), HealthCheck {

    open fun hentOppfolgingsstatus(fnr: Foedselsnummer): OppfolgingStatusData {
        val url = "$baseUrl/oppfolging?fnr=${fnr.stringValue()}"
        val headers = makeHeadersIfKnownSessionCookieIsPresent()

        logger.info("Raw cookies found ${servletRequest().getHeader(HttpHeaders.COOKIE)}")
        logger.info("Cookie-objects found: ${servletRequest().cookies.map{ it.name }}")

        return get(url, headers, OppfolgingStatusData::class.java) { e ->
            when (e) {
                is RestException -> HentOppfolgingStatusException("Hent oppfølgingstatus feilet med status: " + e.code)
                else -> null
            }
        }.also {
            metricsService.registrer(HENT_OPPFOLGING)
        }
    }

    private fun makeHeadersIfKnownSessionCookieIsPresent(): List<Pair<String, String>> =
        servletRequest()
            .cookies
            ?.asList()
            ?.firstOrNull { it.name in KNOWN_AUTH_COOKIES }
            ?.value
            ?.let { listOf(HttpHeaders.COOKIE to it) } ?: emptyList()


    open fun reaktiverBruker(fnr: Fnr) {
        val url = "$baseUrl/oppfolging/reaktiverbruker"
        post(url, fnr, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        metricsService.registrer(REAKTIVER_BRUKER)
    }

    open fun aktiverBruker(aktiverBrukerData: AktiverBrukerData) {
        val url = "$baseUrl/oppfolging/aktiverbruker"
        post(url, aktiverBrukerData, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        metricsService.registrer(AKTIVER_BRUKER)
    }

    fun settOppfolgingSykmeldt(sykmeldtBrukerType: SykmeldtBrukerType, fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/aktiverSykmeldt?fnr=${fnr.stringValue()}"
        post(url, sykmeldtBrukerType, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        metricsService.registrer(OPPFOLGING_SYKMELDT)
    }

    private fun aktiveringFeilMapper(e: Exception): RuntimeException? =
        when (e) {
            is ForbiddenException -> {
                val feil = mapper(objectMapper.readValue(e.response!!))
                logger.warn("Feil ved (re)aktivering av bruker: ${feil.name}")
                metricsService.registrer(AKTIVER_BRUKER_FEIL, Tag.of("aarsak", feil.name))
                AktiverBrukerException(feil)
            }
            else -> {
                logger.error("Uhåndtert feil ved aktivering av bruker: ${e.message}", e)
                metricsService.registrer(OPPFOLGING_FEIL, Tag.of("aarsak", e.message ?: "ukjent"))
                null
            }
        }

    private fun getServiceAuthorizationHeader(): List<Pair<String, String>> =
        listOf(HttpHeaders.AUTHORIZATION to "Bearer ${tokenProvider()}")

    private fun mapper(aktiverBrukerFeilDto: AktiverBrukerFeilDto): AktiverBrukerFeil {
        return when (aktiverBrukerFeilDto.type) {
            ArenaFeilType.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET -> AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET
            ArenaFeilType.BRUKER_MANGLER_ARBEIDSTILLATELSE -> AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
            ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES -> AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES
            ArenaFeilType.BRUKER_ER_UKJENT -> AktiverBrukerFeil.BRUKER_ER_UKJENT
            else -> throw IllegalStateException("Ukjent feil fra Arena: $aktiverBrukerFeilDto")
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), client)
    }

    companion object {
        private val KNOWN_AUTH_COOKIES = listOf(
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME,
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME,
            Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME
        )
    }
}