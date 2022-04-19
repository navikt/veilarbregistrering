package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.bruker.AdressebeskyttelseGradering
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.internBrukerstotte
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway

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
    private val metricsService: MetricsService
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
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.GeografiskTilknytning_Feilet)
            null
        }

        if (geografiskTilknytning != null) {
            if (geografiskTilknytning.byMedBydeler()) {
                logger.info(
                    "Fant {} som er en by med bydeler -> sender oppgave til intern brukerstøtte",
                    geografiskTilknytning
                )
                metricsService.registrer(
                    Events.OPPGAVE_ROUTING_EVENT,
                    RoutingStep.GeografiskTilknytning_ByMedBydel_Funnet
                )
                return internBrukerstotte()
            }
            if (!geografiskTilknytning.utland()) {
                logger.info("Fant {} -> overlater til oppgave-api å route selv", geografiskTilknytning)
                metricsService.registrer(
                    Events.OPPGAVE_ROUTING_EVENT,
                    RoutingStep.GeografiskTilknytning_Funnet
                )
                return null
            }
            logger.info("Fant {} -> forsøker å finne enhetsnr via arbeidsforhold", geografiskTilknytning)
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.GeografiskTilknytning_Utland)
        }
        return try {
            hentEnhetsnummerForSisteArbeidsforholdTil(bruker)?.also {
                metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_Funnet)
            } ?: internBrukerstotte()
        } catch (e: RuntimeException) {
            logger.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e)
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_Feilet)
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
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.SisteArbeidsforhold_IkkeFunnet)
            return null
        }
        val organisasjonsnummer = flereArbeidsforhold.sisteUtenDefault()?.organisasjonsnummer()

        if (organisasjonsnummer == null) {
            logger.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet")
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.OrgNummer_ikkeFunnet)
            return null
        }
        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer)
        if (organisasjonsdetaljer == null) {
            logger.warn(
                "Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: $organisasjonsnummer",
            )
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.OrgDetaljer_IkkeFunnet)
            return null
        }
        val kommune = organisasjonsdetaljer.kommunenummer()
        if (kommune == null) {
            logger.warn("Fant ingen muligKommunenummer knyttet til organisasjon")
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Kommunenummer_IkkeFunnet)
            return null
        }
        if (kommune.kommuneMedBydeler()) {
            logger.info(
                "Fant kommunenummer {} som er tilknyttet kommune med byder -> tildeler den til intern brukerstøtte.",
                kommune.kommunenummer
            )
            return internBrukerstotte()
        }
        return norg2Gateway.hentEnhetFor(kommune) ?: let {
            logger.warn("Fant ingen enhetsnummer knyttet til kommunenummer: {}", kommune.kommunenummer)
            metricsService.registrer(Events.OPPGAVE_ROUTING_EVENT, RoutingStep.Enhetsnummer_IkkeFunnet)
            null
        }
    }

}