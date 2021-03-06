package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.fo.veilarbregistrering.metrics.Events.*;
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
    private final PersonGateway personGateway;
    private final PdlOppslagGateway pdlOppslagGateway;
    private final InfluxMetricsService influxMetricsService;

    public OppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway,
            PdlOppslagGateway pdlOppslagGateway,
            InfluxMetricsService influxMetricsService) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norg2Gateway = norg2Gateway;
        this.personGateway = personGateway;
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.influxMetricsService = influxMetricsService;
    }

    public Optional<Enhetnr> hentEnhetsnummerFor(Bruker bruker) {
        AdressebeskyttelseGradering adressebeskyttelseGradering = hentAdressebeskyttelse(bruker);
        if (adressebeskyttelseGradering.erGradert()) {
            return adressebeskyttelseGradering.getEksplisittRoutingEnhet();
        }

        Optional<GeografiskTilknytning> geografiskTilknytning;
        try {
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker.getGjeldendeFoedselsnummer());
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet", e);
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Feilet);
            geografiskTilknytning = Optional.empty();
        }

        if (geografiskTilknytning.isPresent()) {

            GeografiskTilknytning gk = geografiskTilknytning.get();

            if (gk.byMedBydeler()) {
                LOG.info("Fant {} som er en by med bydeler -> sender oppgave til intern brukerstøtte", gk);
                influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_ByMedBydel_Funnet);
                return Optional.of(Enhetnr.Companion.internBrukerstotte());
            }

            if (!gk.utland()) {
                LOG.info("Fant {} -> overlater til oppgave-api å route selv", gk);
                influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Funnet);
                return Optional.empty();
            }

            LOG.info("Fant {} -> forsøker å finne enhetsnr via arbeidsforhold", gk);
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Utland);
        }

        try {
            Optional<Enhetnr> enhetsnr = hentEnhetsnummerForSisteArbeidsforholdTil(bruker);
            if (enhetsnr.isPresent()) {
                influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Funnet);
                return enhetsnr;
            }
            return of(Enhetnr.Companion.internBrukerstotte());
        } catch (RuntimeException e) {
            LOG.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e);
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Feilet);
            return Optional.empty();
        }
    }

    private AdressebeskyttelseGradering hentAdressebeskyttelse(Bruker bruker) {
        try {
            Optional<Person> person = pdlOppslagGateway.hentPerson(bruker.getAktorId());
            return person.map(Person::getAdressebeskyttelseGradering)
                    .orElse(AdressebeskyttelseGradering.UKJENT);

        } catch (Exception e) {
            LOG.warn("Feil ved uthenting av adressebeskyttelse fra PDL", e);
            return AdressebeskyttelseGradering.UKJENT;
        }
    }

    public Optional<Enhetnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer());
        if (flereArbeidsforhold.sisteUtenNoeEkstra().isEmpty()) {
            LOG.warn("Fant ingen arbeidsforhold knyttet til bruker");
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, SisteArbeidsforhold_IkkeFunnet);
            return Optional.empty();
        }
        Optional<Organisasjonsnummer> organisasjonsnummer = flereArbeidsforhold.sisteUtenNoeEkstra()
                .map(Arbeidsforhold::getOrganisasjonsnummer)
                .orElseThrow(IllegalStateException::new);
        if (organisasjonsnummer.isEmpty()) {
            LOG.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet");
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, OrgNummer_ikkeFunnet);
            return Optional.empty();
        }

        Optional<Organisasjonsdetaljer> organisasjonsdetaljer = Optional.ofNullable(enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer.get()));
        if (organisasjonsdetaljer.isEmpty()) {
            LOG.warn("Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: {}", organisasjonsnummer.get().asString());
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, OrgDetaljer_IkkeFunnet);
            return Optional.empty();
        }

        Optional<Kommunenummer> muligKommunenummer = organisasjonsdetaljer
                .map(Organisasjonsdetaljer::kommunenummer)
                .orElseThrow(IllegalStateException::new);
        if (muligKommunenummer.isEmpty()) {
            LOG.warn("Fant ingen muligKommunenummer knyttet til organisasjon");
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, Kommunenummer_IkkeFunnet);
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
            influxMetricsService.reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_IkkeFunnet);
        }
        return enhetsnr;
    }
}
