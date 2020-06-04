package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.*;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.UTVANDRET;
import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveRouterTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"), AktorId.valueOf("32235352"));

    @Test
    public void ingen_arbeidsforhold_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> FlereArbeidsforhold.of(Collections.emptyList());
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void ingen_enhet_for_orgnummer_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void ingen_navenhet_for_organisasjon_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Arrays.asList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();
        PersonGateway personGateway = foedselsnummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void enhetsnummer_skal_returneres_nar_alle_koblingen_til_arbeidsforhold_er_komplett() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Arrays.asList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = kommunenummer -> Optional.of(Enhetsnr.of("232"));
        PersonGateway personGateway = foedselsnummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetsnr.of("232"));
    }

    @Test
    public void geografisk_tilknytning_med_unntak_av_landkode_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("030106"));

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void geografisk_tilknytning_med_landkode_skal_bruke_arbeidsforhold_til_routing() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();

        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null));

        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.of(Organisasjonsdetaljer.of(
                Arrays.asList(forretningsadresse), Collections.emptyList()));

        Norg2Gateway norg2Gateway = kommunenummer -> Optional.of(Enhetsnr.of("232"));
        PersonGateway personGateway = foedselsnummer -> Optional.of(GeografiskTilknytning.of("DNK"));

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER, UTVANDRET);

        assertThat(enhetsnr).hasValue(Enhetsnr.of("232"));
    }
}
