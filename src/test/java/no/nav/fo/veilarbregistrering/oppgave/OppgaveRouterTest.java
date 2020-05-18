package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.*;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
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
import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveRouterTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"), AktorId.valueOf("32235352"));

    @Test
    public void ingen_arbeidsforhold_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> FlereArbeidsforhold.of(Collections.emptyList());
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(BRUKER);

        assertThat(enhetsnr).isEmpty();
    }

    @Test
    public void ingen_enhet_for_orgnummer_skal_gi_empty_enhetsnummer() {
        ArbeidsforholdGateway arbeidsforholdGateway = fnr -> flereArbeidsforholdTilfeldigSortert();
        EnhetGateway enhetGateway = organisasjonsnummer -> Optional.empty();
        Norg2Gateway norg2Gateway = kommunenummer -> Optional.empty();

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(BRUKER);

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

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(BRUKER);

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

        OppgaveRouter oppgaveRouter = new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(BRUKER);

        assertThat(enhetsnr).hasValue(Enhetsnr.of("232"));
    }
}
