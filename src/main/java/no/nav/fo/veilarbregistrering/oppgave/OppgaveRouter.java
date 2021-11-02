package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.fo.veilarbregistrering.metrics.Events.OPPGAVE_ROUTING_EVENT;
import static no.nav.fo.veilarbregistrering.oppgave.RoutingStep.*;

/**
 * <p>Har som hovedoppgave å route Oppgaver til riktig enhet.</p>
 *
 * <p>Oppgave-tjenesten router i utgangspunktet oppgaver selv, men for utvandrede brukere som
 * ikke ligger inne med Norsk adresse og tilknytning til NAV-kontor må vi gå via tidligere
 * arbeidsgiver.</p>
 *
 * <p>Geografisk tilknytning kan gi verdier ift. landkode, fylke og bydel. Gitt landkode,
 * forsøker vi å gå via tidligere arbeidsforhold</p>
 */
public class OppgaveRouter {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRouter.class);

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final EnhetGateway enhetGateway;
    private final Norg2Gateway norg2Gateway;
    private final PdlOppslagGateway pdlOppslagGateway;
    private final PrometheusMetricsService prometheusMetricsService;

    public OppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PdlOppslagGateway pdlOppslagGateway,
            PrometheusMetricsService prometheusMetricsService) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norg2Gateway = norg2Gateway;
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.prometheusMetricsService = prometheusMetricsService;
    }

    public Optional<Enhetnr> hentEnhetsnummerFor(Bruker bruker) {
        AdressebeskyttelseGradering adressebeskyttelseGradering = hentAdressebeskyttelse(bruker);
        if (adressebeskyttelseGradering.erGradert()) {
            return Optional.ofNullable(adressebeskyttelseGradering.getEksplisittRoutingEnhet());
        }

        Optional<GeografiskTilknytning> geografiskTilknytning;
        try {
            geografiskTilknytning = pdlOppslagGateway.hentGeografiskTilknytning(bruker.getAktorId());
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet", e);
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Feilet);
            geografiskTilknytning = Optional.empty();
        }

        if (geografiskTilknytning.isPresent()) {

            GeografiskTilknytning gk = geografiskTilknytning.get();

            if (gk.byMedBydeler()) {
                LOG.info("Fant {} som er en by med bydeler -> sender oppgave til intern brukerstøtte", gk);
                prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_ByMedBydel_Funnet);
                return Optional.of(Enhetnr.Companion.internBrukerstotte());
            }

            if (!gk.utland()) {
                LOG.info("Fant {} -> overlater til oppgave-api å route selv", gk);
                prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Funnet);
                return Optional.empty();
            }

            LOG.info("Fant {} -> forsøker å finne enhetsnr via arbeidsforhold", gk);
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Utland);
        }

        try {
            Optional<Enhetnr> enhetsnr = hentEnhetsnummerForSisteArbeidsforholdTil(bruker);
            if (enhetsnr.isPresent()) {
                prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Funnet);
                return enhetsnr;
            }
            return of(Enhetnr.Companion.internBrukerstotte());
        } catch (RuntimeException e) {
            LOG.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e);
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Feilet);
            return Optional.empty();
        }
    }

    private AdressebeskyttelseGradering hentAdressebeskyttelse(Bruker bruker) {
        try {
            Optional<Person> person = Optional.ofNullable(pdlOppslagGateway.hentPerson(bruker.getAktorId()));
            return person.map(Person::getAdressebeskyttelseGradering)
                    .orElse(AdressebeskyttelseGradering.UKJENT);

        } catch (Exception e) {
            LOG.warn("Feil ved uthenting av adressebeskyttelse fra PDL", e);
            return AdressebeskyttelseGradering.UKJENT;
        }
    }

    public Optional<Enhetnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer());
        if (flereArbeidsforhold.sisteUtenDefault() == null) {
            LOG.warn("Fant ingen arbeidsforhold knyttet til bruker");
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, SisteArbeidsforhold_IkkeFunnet);
            return Optional.empty();
        }
        Optional<Organisasjonsnummer> organisasjonsnummer = Optional.ofNullable(flereArbeidsforhold.sisteUtenDefault())
                .map(Arbeidsforhold::getOrganisasjonsnummer)
                .orElseThrow(IllegalStateException::new);
        if (organisasjonsnummer.isEmpty()) {
            LOG.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet");
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, OrgNummer_ikkeFunnet);
            return Optional.empty();
        }

        Optional<Organisasjonsdetaljer> organisasjonsdetaljer = Optional.ofNullable(enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer.get()));
        if (organisasjonsdetaljer.isEmpty()) {
            LOG.warn("Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: {}", organisasjonsnummer.get().asString());
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, OrgDetaljer_IkkeFunnet);
            return Optional.empty();
        }

        Optional<Kommunenummer> muligKommunenummer = organisasjonsdetaljer
                .map(Organisasjonsdetaljer::kommunenummer)
                .orElseThrow(IllegalStateException::new);
        if (muligKommunenummer.isEmpty()) {
            LOG.warn("Fant ingen muligKommunenummer knyttet til organisasjon");
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, Kommunenummer_IkkeFunnet);
            return Optional.empty();
        }

        Kommunenummer kommunenummer = muligKommunenummer.orElseThrow(IllegalStateException::new);
        if (kommunenummer.kommuneMedBydeler()) {
            LOG.info("Fant kommunenummer {} som er tilknyttet kommune med byder -> tildeler den til intern brukerstøtte.", kommunenummer.asString());
            return of(Enhetnr.Companion.internBrukerstotte());
        }

        Optional<Enhetnr> enhetsnr = norg2Gateway.hentEnhetFor(kommunenummer);
        if (enhetsnr.isEmpty()) {
            LOG.warn("Fant ingen enhetsnummer knyttet til kommunenummer: {}", kommunenummer.asString());
            prometheusMetricsService.registrer(OPPGAVE_ROUTING_EVENT, Enhetsnummer_IkkeFunnet);
        }
        return enhetsnr;
    }
}
