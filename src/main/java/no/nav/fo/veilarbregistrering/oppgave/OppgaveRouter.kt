package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.internBrukerstotte
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import java.util.*

/**
 *
 * Har som hovedoppgave å route Oppgaver til riktig enhet.
 *
 *
 * Oppgave-tjenesten router i utgangspunktet oppgaver selv, men for utvandrede brukere som
 * ikke ligger inne med Norsk adresse og tilknytning til NAV-kontor må vi gå via tidligere
 * arbeidsgiver.
 *
 *
 * Geografisk tilknytning kan gi verdier ift. landkode, fylke og bydel. Gitt landkode,
 * forsøker vi å gå via tidligere arbeidsforhold
 */


class OppgaveRouter(
    private val arbeidsforholdGateway: ArbeidsforholdGateway,
    private val enhetGateway: EnhetGateway,
    private val norg2Gateway: Norg2Gateway,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val prometheusMetricsService: PrometheusMetricsService
) {
    fun hentEnhetsnummerFor(bruker: Bruker): Enhetnr? {
        val adressebeskyttelseGradering = hentAdressebeskyttelse(bruker)
        if (adressebeskyttelseGradering.erGradert()) {
            return adressebeskyttelseGradering.eksplisittRoutingEnhet
        }

        val geografiskTilknytning: GeografiskTilknytning? = try {
            pdlOppslagGateway.hentGeografiskTilknytning(bruker.aktorId)
        } catch (e: RuntimeException) {
            logger.warn("Henting av geografisk tilknytning feilet", e)
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.GeografiskTilknytning_Feilet)
            null
        }

        if (geografiskTilknytning != null) {
            if (geografiskTilknytning.byMedBydeler()) {
                logger.info(
                    "Fant {} som er en by med bydeler -> sender oppgave til intern brukerstøtte",
                    geografiskTilknytning
                )
                prometheusMetricsService.registrer(
                    Events.OPPGAVE_ROUTING_EVENT,
                    RoutingStep.GeografiskTilknytning_ByMedBydel_Funnet
                )
                return internBrukerstotte()
            }
            if (!geografiskTilknytning.utland()) {
                logger.info("Fant {} -> overlater til oppgave-api å route selv", geografiskTilknytning)
                prometheusMetricsService.registrer(
                    Events.OPPGAVE_ROUTING_EVENT,
                    RoutingStep.GeografiskTilknytning_Funnet
                )
                return null
            }
            logger.info("Fant {} -> forsøker å finne enhetsnr via arbeidsforhold", geografiskTilknytning)
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.GeografiskTilknytning_Utland)
        }
        return try {
            hentEnhetsnummerForSisteArbeidsforholdTil(bruker)?.also {
                prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_Funnet)
            } ?: internBrukerstotte()
        } catch (e: RuntimeException) {
            logger.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e)
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_Feilet)
            null
        }
    }

    private fun hentAdressebeskyttelse(bruker: Bruker): AdressebeskyttelseGradering {
        return try {
            pdlOppslagGateway.hentPerson(bruker.aktorId)
                ?.adressebeskyttelseGradering
                ?: (AdressebeskyttelseGradering.UKJENT)
        } catch (e: Exception) {
            logger.warn("Feil ved uthenting av adressebeskyttelse fra PDL", e)
            AdressebeskyttelseGradering.UKJENT
        }
    }

    private fun hentEnhetsnummerForSisteArbeidsforholdTil(bruker: Bruker): Enhetnr? {
        val flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.gjeldendeFoedselsnummer)
        if (flereArbeidsforhold.sisteUtenDefault() == null) {
            logger.warn("Fant ingen arbeidsforhold knyttet til bruker")
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.SisteArbeidsforhold_IkkeFunnet)
            return null
        }
        val organisasjonsnummer = Optional.ofNullable(flereArbeidsforhold.sisteUtenDefault())
            .map(Arbeidsforhold::organisasjonsnummer)
            .orElseThrow { IllegalStateException() }
        if (organisasjonsnummer.isEmpty) {
            logger.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet")
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.OrgNummer_ikkeFunnet)
            return null
        }
        val organisasjonsdetaljer = Optional.ofNullable(
            enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer.get())
        )
        if (organisasjonsdetaljer.isEmpty) {
            logger.warn(
                "Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: {}",
                organisasjonsnummer.get().asString()
            )
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.OrgDetaljer_IkkeFunnet)
            return null
        }
        val muligKommunenummer = Optional.ofNullable(organisasjonsdetaljer
            .map { obj: Organisasjonsdetaljer -> obj.kommunenummer() }
            .orElseThrow { IllegalStateException() })
        if (muligKommunenummer.isEmpty) {
            logger.warn("Fant ingen muligKommunenummer knyttet til organisasjon")
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Kommunenummer_IkkeFunnet)
            return null
        }
        val kommune = muligKommunenummer.orElseThrow { IllegalStateException() }
        if (kommune.kommuneMedBydeler()) {
            logger.info(
                "Fant kommunenummer {} som er tilknyttet kommune med byder -> tildeler den til intern brukerstøtte.",
                kommune.kommunenummer
            )
            return internBrukerstotte()
        }
        return norg2Gateway.hentEnhetFor(kommune).orElseGet {
            logger.warn("Fant ingen enhetsnummer knyttet til kommunenummer: {}", kommune.kommunenummer)
            prometheusMetricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_IkkeFunnet)
            null
        }
    }

}