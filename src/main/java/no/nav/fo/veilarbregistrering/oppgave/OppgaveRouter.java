package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_ROUTING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportTags;
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

    public OppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norg2Gateway = norg2Gateway;
        this.personGateway = personGateway;
    }

    public Optional<Enhetsnr> hentEnhetsnummerFor(Bruker bruker, OppgaveType oppgaveType) {
        if (!oppgaveType.equals(OppgaveType.UTVANDRET)) {
            return Optional.empty();
        }

        Optional<GeografiskTilknytning> geografiskTilknytning;
        try {
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker.getFoedselsnummer());
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet", e);
            reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Feilet);
            geografiskTilknytning = Optional.empty();
        }

        if (geografiskTilknytning.isPresent()) {

            GeografiskTilknytning gk = geografiskTilknytning.get();

            if (!gk.utland()) {
                LOG.info("Fant {} -> overlater til oppgave-api å route selv", gk);
                reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Funnet);
                return Optional.empty();
            }

            LOG.info("Fant {} -> forsøker å finne enhetsnr via arbeidsforhold", gk);
            reportTags(OPPGAVE_ROUTING_EVENT, GeografiskTilknytning_Utland);
        }

        try {
            Optional<Enhetsnr> enhetsnr = hentEnhetsnummerForSisteArbeidsforholdTil(bruker);
            if (enhetsnr.isPresent()) {
                reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Funnet);
                return enhetsnr;
            }
            return of(Enhetsnr.internBrukerstotte());
        } catch (RuntimeException e) {
            LOG.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e);
            reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_Feilet);
            return Optional.empty();
        }
    }

    public Optional<Enhetsnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        if (!flereArbeidsforhold.sisteUtenNoeEkstra().isPresent()) {
            LOG.warn("Fant ingen arbeidsforhold knyttet til bruker");
            reportTags(OPPGAVE_ROUTING_EVENT, SisteArbeidsforhold_IkkeFunnet);
            return Optional.empty();
        }
        Optional<Organisasjonsnummer> organisasjonsnummer = flereArbeidsforhold.sisteUtenNoeEkstra()
                .map(sisteArbeidsforhold -> sisteArbeidsforhold.getOrganisasjonsnummer())
                .orElseThrow(IllegalStateException::new);
        if (!organisasjonsnummer.isPresent()) {
            LOG.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet");
            reportTags(OPPGAVE_ROUTING_EVENT, OrgNummer_ikkeFunnet);
            return Optional.empty();
        }

        Optional<Organisasjonsdetaljer> organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer.get());
        if (!organisasjonsdetaljer.isPresent()) {
            LOG.warn("Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: {}", organisasjonsnummer.get().asString());
            reportTags(OPPGAVE_ROUTING_EVENT, OrgDetaljer_IkkeFunnet);
            return Optional.empty();
        }

        Optional<Kommunenummer> kommunenummer = organisasjonsdetaljer
                .map(a -> a.kommunenummer())
                .orElseThrow(IllegalStateException::new);
        if (!kommunenummer.isPresent()) {
            LOG.warn("Fant ingen kommunenummer knyttet til organisasjon");
            reportTags(OPPGAVE_ROUTING_EVENT, Kommunenummer_IkkeFunnet);
            return Optional.empty();
        }

        Optional<Enhetsnr> enhetsnr = norg2Gateway.hentEnhetFor(kommunenummer
                .orElseThrow(IllegalStateException::new));
        if (!enhetsnr.isPresent()) {
            LOG.warn("Fant ingen enhetsnummer knyttet til kommunenummer: {}", kommunenummer.get().asString());
            reportTags(OPPGAVE_ROUTING_EVENT, Enhetsnummer_IkkeFunnet);
        }
        return enhetsnr;
    }
}
