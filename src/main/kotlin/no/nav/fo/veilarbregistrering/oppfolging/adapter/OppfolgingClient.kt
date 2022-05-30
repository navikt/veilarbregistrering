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
import no.nav.fo.veilarbregistrering.http.Headers
import no.nav.fo.veilarbregistrering.http.Json
import no.nav.fo.veilarbregistrering.http.buildHttpClient
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.secureLogger
import no.nav.fo.veilarbregistrering.metrics.Events.*
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import no.nav.fo.veilarbregistrering.oauth2.AadOboService
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.HttpHeaders

open class OppfolgingClient(
    private val objectMapper: ObjectMapper,
    private val metricsService: MetricsService,
    private val baseUrl: String,
    private val aadOboService: AadOboService,
    private val tokenProvider: () -> String,

    ) : TimedMetric(metricsService), HealthCheck {

    open fun reaktiverBruker(fnr: Fnr) {
        val url = "$baseUrl/oppfolging/reaktiverbruker"
        val request = Request.Builder().url(url)
            .headers(Headers.buildHeaders(getServiceAuthorizationHeader()))
            .method("POST", RequestBody.create(Json, objectMapper.writeValueAsString(fnr)))
            .build()
        doTimedCall(REAKTIVER_BRUKER) {
            client.newCall(request).execute().use { reaktiveringResponsMapper(it, fnr) }
        }
    }

    private fun reaktiveringResponsMapper(response: Response, fnr: Fnr) {
        if (response.isSuccessful) return
        else {
            when (response.code()) {
                403 -> {
                    val feil = mapper(objectMapper.readValue(response.body()!!.string()))
                    if (feil == AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES_FORENKLET) {
                        secureLogger.info("Bruker med fnr ${fnr.fnr} fikk feil ved reaktivering: $feil")
                    }
                    metricsService.registrer(REAKTIVER_BRUKER_FEIL, feil)
                    throw AktiverBrukerException("Feil ved reaktivering av bruker: ${feil.name}", feil)
                }
                else -> {
                    metricsService.registrer(OPPFOLGING_FEIL, Tag.of("aarsak", response.code().toString()))
                    throw RuntimeException("Feil ved reaktivering av bruker: ${response.code()}, ${response.body()?.string()}")
                }
            }
        }
    }

    open fun aktiverBruker(aktiverBrukerData: AktiverBrukerData) {
        val url = "$baseUrl/oppfolging/aktiverbruker"
        val request = Request.Builder().url(url)
            .headers(Headers.buildHeaders(getServiceAuthorizationHeader()))
            .method("POST", RequestBody.create(Json, objectMapper.writeValueAsString(aktiverBrukerData)))
            .build()
        doTimedCall(AKTIVER_BRUKER) {
            client.newCall(request).execute().use { aktiveringResponsMapper(it) }
        }
    }

    private fun aktiveringResponsMapper(response: Response) {
        if (response.isSuccessful) return
        else {
            when (response.code()) {
                403 -> {
                    val feil = mapper(objectMapper.readValue(response.body()!!.string()))
                    metricsService.registrer(AKTIVER_BRUKER_FEIL, feil)
                    throw AktiverBrukerException("Feil ved aktivering av bruker: ${feil.name}", feil)
                }
                else -> {
                    metricsService.registrer(OPPFOLGING_FEIL, Tag.of("aarsak", response.code().toString()))
                    throw RuntimeException("Feil ved aktivering av bruker: ${response.code()}, ${response.body()?.string()}")
                }
            }
        }
    }

    fun aktiverSykmeldt(sykmeldtBrukerType: SykmeldtBrukerType, fnr: Foedselsnummer) {
        val url = "$baseUrl/oppfolging/aktiverSykmeldt?fnr=${fnr.stringValue()}"
        val request = Request.Builder().url(url)
            .headers(Headers.buildHeaders(getServiceAuthorizationHeader()))
            .method("POST", RequestBody.create(Json, objectMapper.writeValueAsString(sykmeldtBrukerType)))
            .build()
        doTimedCall(OPPFOLGING_SYKMELDT) {
            client.newCall(request).execute().use { aktiveringSykmeldtResponsMapper(it) }
        }
    }


    private fun aktiveringSykmeldtResponsMapper(response: Response) {
        if (response.isSuccessful) return
        else {
            when (response.code()) {
                403 -> {
                    val feil = mapper(objectMapper.readValue(response.body()!!.string()))
                    metricsService.registrer(OPPFOLGING_SYKMELDT_FEIL, feil)
                    throw AktiverBrukerException("Feil ved aktivering av sykmeldt bruker: ${feil.name}", feil)
                }
                else -> {
                    metricsService.registrer(OPPFOLGING_FEIL, Tag.of("aarsak", response.code().toString()))
                    throw RuntimeException("Feil ved aktivering av sykmeldt bruker: ${response.code()}, ${response.body()?.string()}")
                }
            }
        }
    }

    fun erBrukerUnderOppfolging(fodselsnummer: Foedselsnummer): ErUnderOppfolgingDto {
        val url = "$baseUrl/v2/oppfolging?fnr=${fodselsnummer.stringValue()}"
        val request = Request.Builder().url(url)
            .headers(Headers.buildHeaders(getAuthorizationFromCookieOrResolveOboToken()))
            .build()
        return doTimedCall {
            client.newCall(request).execute().use {
                if (it.isSuccessful) {
                    it.body()?.string()?.let { bodyString ->
                        objectMapper.readValue(bodyString, ErUnderOppfolgingDto::class.java)
                    } ?: throw RuntimeException("Unexpected empty body")
                } else throw SammensattOppfolgingStatusException("Feil ved kall til oppfolging-api v2: ${it.code()}")
            }
        }
    }

    private fun getAuthorizationFromCookieOrResolveOboToken(): List<Pair<String, String>> {
        if (aadOboService.erAzureAdToken()) {
            return listOf(("Authorization" to "Bearer ${aadOboService.getAccessToken(oppfolgingApi)}"))
        }

        return listOf(
            servletRequest().getHeader(HttpHeaders.COOKIE)?.let { HttpHeaders.COOKIE to it }
                ?: (HttpHeaders.COOKIE to "selvbetjening-idtoken=${servletRequest().getHeader(HttpHeaders.AUTHORIZATION).removePrefix("Bearer ")}")
        )
    }

    private fun getServiceAuthorizationHeader(): List<Pair<String, String>> =
        listOf(HttpHeaders.AUTHORIZATION to "Bearer ${tokenProvider()}")

    private fun mapper(aktiverBrukerFeilDto: AktiverBrukerFeilDto): AktiverBrukerFeil {
        return when (aktiverBrukerFeilDto.type) {
            ArenaFeilType.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET -> AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET
            ArenaFeilType.BRUKER_MANGLER_ARBEIDSTILLATELSE -> AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
            ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES -> AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES
            ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES_FORENKLET -> AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES_FORENKLET
            ArenaFeilType.BRUKER_ER_UKJENT -> AktiverBrukerFeil.BRUKER_ER_UKJENT
            else -> throw IllegalStateException("Ukjent feil fra Arena: $aktiverBrukerFeilDto")
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), client)
    }

    override fun value() = "veilarboppfolging"

    companion object {
        val client: OkHttpClient = buildHttpClient {
            readTimeout(120L, TimeUnit.SECONDS)
        }
    }
}

