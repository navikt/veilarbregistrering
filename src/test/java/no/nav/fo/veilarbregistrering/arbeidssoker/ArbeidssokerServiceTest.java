package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArbeidssokerServiceTest {

    public static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    public static final Foedselsnummer FOEDSELSNUMMER_2 = Foedselsnummer.of("11234567890");

    private ArbeidssokerService arbeidssokerService;
    private UnleashService unleashService;

    @BeforeEach
    public void setup() {
        unleashService = mock(UnleashService.class);
        arbeidssokerService = new ArbeidssokerService(
                new StubArbeidssokerRepository(),
                new StubFormidlingsgruppeGateway(),
                unleashService);
    }

    @Test
    public void hentArbeidssokerperioder_skal_returnere_perioder_sortert_etter_fradato() {

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 5, 1));

        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(true);

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(FOEDSELSNUMMER, forespurtPeriode);
        assertThat(arbeidssokerperiodes).hasSize(4);
        assertThat(arbeidssokerperiodes).containsSequence(
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_1,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_2,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_4);
    }

    @Test
    public void hentArbeidssokerperioder_skal_hente_fra_ords_toggle_ikke_er_skrudd_paa() {
        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 5, 1));

        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(false);

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(FOEDSELSNUMMER, forespurtPeriode);

        assertThat(arbeidssokerperiodes).hasSize(1);
        assertThat(arbeidssokerperiodes).contains(StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_1);

    }

    private static class StubArbeidssokerRepository implements ArbeidssokerRepository {

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
            Map<Foedselsnummer, Arbeidssokerperioder> map = new HashMap<>();

            Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_2));
            map.put(FOEDSELSNUMMER, arbeidssokerperioder);
            map.put(FOEDSELSNUMMER_2, new Arbeidssokerperioder(Collections.emptyList()));

            return map.get(foedselsnummer);
        }
    }

    private class StubFormidlingsgruppeGateway implements FormidlingsgruppeGateway {

        @Override
        public Arbeidssokerperioder finnArbeissokerperioder(Foedselsnummer foedselsnummer, Periode periode) {
            Map<Foedselsnummer, Arbeidssokerperioder> map = new HashMap<>();
            map.put(FOEDSELSNUMMER, new Arbeidssokerperioder(asList(StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_1)));
            return map.get(foedselsnummer);
        }
    }
}
