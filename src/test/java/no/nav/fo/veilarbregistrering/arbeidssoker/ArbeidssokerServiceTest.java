package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArbeidssokerServiceTest {

    public static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("123445678911");

    ArbeidssokerService arbeidssokerService;

    @BeforeEach
    public void setup() {
        UnleashService unleashService = mock(UnleashService.class);
        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(true);
        this.arbeidssokerService = new ArbeidssokerService(
                new CustomArbeidssokerRepository(),
                (foedselsnummer, periode) -> null,
                unleashService);
    }

    @Test
    public void hentArbeidssokerperioder_skal_returnere_perioder_sortert_etter_fradato() {

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 5, 1));

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(FOEDSELSNUMMER, forespurtPeriode);
        assertThat(arbeidssokerperiodes).hasSize(4);
        assertThat(arbeidssokerperiodes).containsSequence(
                CustomArbeidssokerRepository.ARBEIDSSOKERPERIODE_1,
                CustomArbeidssokerRepository.ARBEIDSSOKERPERIODE_2,
                CustomArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
                CustomArbeidssokerRepository.ARBEIDSSOKERPERIODE_4);
    }

    private static class CustomArbeidssokerRepository implements ArbeidssokerRepository {

        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_1 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_2 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 2, 1), null));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_3 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 3, 1), null));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_4 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 4, 1), null));

        @Override
        public long lagre(EndretFormidlingsgruppeCommand arenaFormidlingsgruppeEvent) {
            return 0;
        }

        @Override
        public Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
            return new Arbeidssokerperioder(Arrays.asList(
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_2));
        }
    }
}
