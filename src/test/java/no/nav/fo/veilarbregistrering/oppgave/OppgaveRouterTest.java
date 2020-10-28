package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert;
import static no.nav.fo.veilarbregistrering.enhet.Kommunenummer.KommuneMedBydel.STAVANGER;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.OPPHOLDSTILLATELSE;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.UTVANDRET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OppgaveRouterTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"), AktorId.of("32235352"));

    @Test
    public void ingen_arbeidsforhold_skal_gi_intern_brukerstotte() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> FlereArbeidsforhold.of(Collections.emptyList());
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"));
    }

    @Test
    public void ingen_enhet_for_orgnummer_skal_gi_intern_brukerstotte() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"));
    }

    @Test
    public void ingen_navenhet_for_organisasjon_skal_gi_intern_brukerstotte() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1240"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"));
    }

    @Test
    public void enhetsnummer_skal_returneres_nar_alle_koblingen_til_arbeidsforhold_er_komplett() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.of("232"));
    }

    @Test
    public void geografisk_tilknytning_med_by_med_bydel_skal_gi_intern_brukerstotte() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("0301"));
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte());
    }

    @Test
    public void geografisk_tilknytning_med_unntak_av_landkode_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("030106"));
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void geografisk_tilknytning_med_landkode_skal_bruke_arbeidsforhold_til_routing() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("DNK"));
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.of("232"));
    }

    @Test
    public void kommunenummer_tilhorende_kommune_med_bydeler_skal_tildeles_intern_brukerstotte() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of(STAVANGER),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("DNK"));
        UnleashService unleashService = mock(UnleashService.class);
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte());
    }


    @Test
    public void routing_for_oppgave_type_oppholdstilatelse_uten_feature_toggle() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);
        UnleashService unleashService = mock(UnleashService.class);

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE);
        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void routing_for_oppgave_type_oppholdstilatelse_med_feature_toggle() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Collections.singletonList(forretningsadresse), Collections.emptyList()));
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();
        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);
        UnleashService unleashService = unleashServiceMedFeatures("veilarbregistrering.utvidetEnhetsoppslagForAlleOppgavetyper");

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE);
        assertThat(enhetsnr).hasValue(Enhetnr.of("232"));
    }

    @Test
    public void brukere_med_adressebeskyttelse_overlates_til_oppgave_api() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = new StubNorg2Gateway();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("030106"));
        UnleashService unleashService = unleashServiceMedFeatures("veilarbregistrering.adressebeskyttelse");

        PdlOppslagGateway pdlOppslagGateway = mock(PdlOppslagGateway.class);

        Person person = Person.of(null, null, null, null, AdressebeskyttelseGradering.STRENGT_FORTROLIG);
        when(pdlOppslagGateway.hentPerson(BRUKER.getAktorId())).thenReturn(Optional.of(person));

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    static class StubNorg2Gateway implements Norg2Gateway {
        @Override
        public Optional<Enhetnr> hentEnhetFor(Kommunenummer kommunenummer) {
            if (Kommunenummer.of("1241").equals(kommunenummer)) {
                return Optional.of(Enhetnr.of("232"));
            }


            if (Kommunenummer.of(STAVANGER).equals(kommunenummer)) {
                return Optional.of(Enhetnr.of("1103"));
            }
            return Optional.empty();
        }

        @Override
        public Map<Enhetnr, NavEnhet> hentAlleEnheter() {
            return null;
        }
    }

    private UnleashService unleashServiceMedFeatures(String... aktiverteFeatures) {
        UnleashService unleashService = mock(UnleashService.class);
        for (String aktivertFeature: aktiverteFeatures) {
            when(unleashService.isEnabled(aktivertFeature)).thenReturn(true);
        }
        return unleashService;
    }
}
