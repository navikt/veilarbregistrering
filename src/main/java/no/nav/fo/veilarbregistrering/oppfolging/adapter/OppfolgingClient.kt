package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.HentOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppfolging.adapter.AktiverBrukerFeilDto.ArenaFeilType
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerException
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil
import javax.ws.rs.core.HttpHeaders

open class OppfolgingClient(
        private val influxMetricsService: InfluxMetricsService,
        private val objectMapper: ObjectMapper,
        private val baseUrl: String,
        private val systemUserTokenProvider: SystemUserTokenProvider
) : AbstractOppfolgingClient(objectMapper), HealthCheck {

    open fun hentOppfolgingsstatus(fnr: Foedselsnummer): OppfolgingStatusData {
        val url = "$baseUrl/oppfolging?fnr=${fnr.stringValue()}"
        val headers = listOf(HttpHeaders.COOKIE to servletRequest().getHeader(HttpHeaders.COOKIE))

        return influxMetricsService.timeAndReport(Events.HENT_OPPFOLGING) {
            get(url, headers, OppfolgingStatusData::class.java) { e ->
                when (e) {
                    is RestException -> HentOppfolgingStatusException("Hent oppfølgingstatus feilet med status: " + e.code)
                    else -> null
                }
            }
        }
    }

    open fun reaktiverBruker(fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/reaktiverbruker"

        influxMetricsService.timeAndReport(Events.REAKTIVER_BRUKER) {
            post(url, Fnr(fnr.stringValue()), getSystemAuthorizationHeaders(), ::aktiveringFeilMapper)
        }
    }

    open fun aktiverBruker(aktiverBrukerData: AktiverBrukerData?) {
        val url = "$baseUrl/oppfolging/aktiverbruker"

        influxMetricsService.timeAndReport(Events.AKTIVER_BRUKER) {
            post(url, aktiverBrukerData, getSystemAuthorizationHeaders(), ::aktiveringFeilMapper)
        }
    }

    fun settOppfolgingSykmeldt(sykmeldtBrukerType: SykmeldtBrukerType?, fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/aktiverSykmeldt?fnr=${fnr.stringValue()}"

        influxMetricsService.timeAndReport(Events.OPPFOLGING_SYKMELDT) {
            post(url, sykmeldtBrukerType, getSystemAuthorizationHeaders(), ::aktiveringFeilMapper)
        }
    }

    private fun aktiveringFeilMapper(e: Exception): RuntimeException? =
        when (e) {
            is ForbiddenException -> {
                val feil = mapper(objectMapper.readValue(e.response!!))
                LOG.warn("Feil ved (re)aktivering av bruker: ${feil.name}")
                AktiverBrukerException(feil)
            }
            else -> {
                LOG.error("Uhåndtert feil ved aktivering av bruker: ${e.message}", e)
                null
            }
        }

    private fun getSystemAuthorizationHeaders() =
        listOf(
            "SystemAuthorization" to systemUserTokenProvider.systemUserToken,
            HttpHeaders.AUTHORIZATION to "Bearer " + systemUserTokenProvider.systemUserToken
        )

    private fun mapper(aktiverBrukerFeilDto: AktiverBrukerFeilDto): AktiverBrukerFeil {
        return when (aktiverBrukerFeilDto.type) {
            ArenaFeilType.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET -> AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET
            ArenaFeilType.BRUKER_MANGLER_ARBEIDSTILLATELSE -> AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
            ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES -> AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES
            ArenaFeilType.BRUKER_ER_UKJENT -> AktiverBrukerFeil.BRUKER_ER_UKJENT
            else -> throw IllegalStateException("Ukjent feil fra Arena: " + aktiverBrukerFeilDto.type)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), client)
    }

    companion object {
        private val LOG = loggerFor<OppfolgingClient>()
    }
}