package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.instrument.Tag
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events.*
import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oauth2.AadOboService
import no.nav.fo.veilarbregistrering.oppfolging.HentOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.ws.rs.core.HttpHeaders

open class OppfolgingClient(
    private val objectMapper: ObjectMapper,
    private val metricsService: PrometheusMetricsService,
    private val baseUrl: String,
    private val aadOboService: AadOboService,
    private val tokenProvider: () -> String,

    ) : AbstractOppfolgingClient(objectMapper), HealthCheck, Metric {

    open fun hentOppfolgingsstatus(fnr: Foedselsnummer): OppfolgingStatusData {
        val url = "$baseUrl/oppfolging?fnr=${fnr.stringValue()}"
        val headers = getAuthorizationFromCookieOrResolveOboToken()

        return get(url, headers, OppfolgingStatusData::class.java) { e ->
            when (e) {
                is RestException -> HentOppfolgingStatusException("Hent oppfølgingstatus feilet med status: " + e.code)
                else -> null
            }
        }.also {
            metricsService.registrer(HENT_OPPFOLGING)
        }
    }

    open fun reaktiverBruker(fnr: Fnr) {
        val url = "$baseUrl/oppfolging/reaktiverbruker"
        post(url, fnr, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        metricsService.registrer(REAKTIVER_BRUKER)
    }

    open fun aktiverBruker(aktiverBrukerData: AktiverBrukerData) {
        val url = "$baseUrl/oppfolging/aktiverbruker"
        val start = Instant.now(Clock.systemDefaultZone())
        post(url, aktiverBrukerData, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        val end = Instant.now(Clock.systemDefaultZone())
        metricsService.registrerTimer(KALL_TREDJEPART, Duration.between(start, end), this)
        metricsService.registrer(AKTIVER_BRUKER)
    }

    fun aktiverSykmeldt(sykmeldtBrukerType: SykmeldtBrukerType, fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/aktiverSykmeldt?fnr=${fnr.stringValue()}"
        post(url, sykmeldtBrukerType, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        metricsService.registrer(OPPFOLGING_SYKMELDT)
    }

    fun erBrukerUnderOppfolging(fodselsnummer: Foedselsnummer): ErUnderOppfolgingDto {
        val url = "$baseUrl/v2/oppfolging?fnr=${fodselsnummer.stringValue()}"
        return get(url, getAuthorizationFromCookieOrResolveOboToken(), ErUnderOppfolgingDto::class.java) {
            SammensattOppfolgingStatusException("Feil ved kall til oppfolging-api v2", it)
        }
    }

    private fun aktiveringFeilMapper(e: Exception): RuntimeException? =
        when (e) {
            is ForbiddenException -> {
                val feil = mapper(objectMapper.readValue(e.response!!))
                logger.warn("Feil ved (re)aktivering av bruker: ${feil.name}")
                metricsService.registrer(AKTIVER_BRUKER_FEIL, feil)
                AktiverBrukerException(feil)
            }
            else -> {
                logger.error("Uhåndtert feil ved aktivering av bruker: ${e.message}", e)
                metricsService.registrer(OPPFOLGING_FEIL, Tag.of("aarsak", e.message ?: "ukjent"))
                null
            }
        }

    private fun getAuthorizationFromCookieOrResolveOboToken(): List<Pair<String, String>> {
        return listOf(
            servletRequest().getHeader(HttpHeaders.COOKIE)?.let { HttpHeaders.COOKIE to it }
                ?: ("Authorization" to "Bearer ${aadOboService.getAccessToken(oppfolgingApi)}")
        )
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

    override fun fieldName() = "tjeneste"
    override fun value() = "veilarboppfolging"
}