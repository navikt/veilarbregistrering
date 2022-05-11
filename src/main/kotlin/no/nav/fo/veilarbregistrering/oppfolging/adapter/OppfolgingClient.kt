package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.instrument.Tag
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AaregRestClient
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.http.Json
import no.nav.fo.veilarbregistrering.http.buildHttpClient
import no.nav.fo.veilarbregistrering.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events.*
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oauth2.AadOboService
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.oppfolging.ReaktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.HttpHeaders

open class OppfolgingClient(
    private val objectMapper: ObjectMapper,
    private val metricsService: MetricsService,
    private val baseUrl: String,
    private val aadOboService: AadOboService,
    private val tokenProvider: () -> String,

    ) : AbstractOppfolgingClient(objectMapper, metricsService), HealthCheck  {

    open fun reaktiverBruker(fnr: Fnr) {
        val url = "$baseUrl/oppfolging/reaktiverbruker"
        val request = Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("oppfolging/reaktiverbruker")
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenProvider()}")
            .method("POST", fnr.let { RequestBody.create(Json, objectMapper.writeValueAsString(it)) })
            .build()

        doTimedCall(REAKTIVER_BRUKER) {
            client.newCall(request).execute().use { response ->
                when (val code = response.code()) {
                    204 -> return@use
                    403 -> throw ReaktiverBrukerException(
                        "Feil ved reaktivering av bruker - ingen tilgang. Response fra oppfølging: ${
                            response.body()?.toString()
                        }"
                    )
                    else -> throw ReaktiverBrukerException(
                        "Feil ved reaktiver av bruker. Responsekode: ${response.code()}. " +
                                "Response fra oppfølging: ${response.body()?.toString()}"
                    )
                }

            }
        }
    }

    open fun aktiverBruker(aktiverBrukerData: AktiverBrukerData) {
        val url = "$baseUrl/oppfolging/aktiverbruker"
        doTimedCall(AKTIVER_BRUKER) {
            post(url, aktiverBrukerData, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        }
    }

    fun aktiverSykmeldt(sykmeldtBrukerType: SykmeldtBrukerType, fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/aktiverSykmeldt?fnr=${fnr.stringValue()}"
        doTimedCall(OPPFOLGING_SYKMELDT) {
            post(url, sykmeldtBrukerType, getServiceAuthorizationHeader(), ::aktiveringFeilMapper)
        }
    }

    fun erBrukerUnderOppfolging(fodselsnummer: Foedselsnummer): ErUnderOppfolgingDto {
        val url = "$baseUrl/v2/oppfolging?fnr=${fodselsnummer.stringValue()}"
        return doTimedCall {
            get(url, getAuthorizationFromCookieOrResolveOboToken(), ErUnderOppfolgingDto::class.java) {
                SammensattOppfolgingStatusException("Feil ved kall til oppfolging-api v2", it)
            }
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

    companion object {
        val client: OkHttpClient = buildHttpClient {
            readTimeout(120L, TimeUnit.SECONDS)
        }

        val emptyHandler: (Exception) -> Nothing? = { null }
    }
}