package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
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

    public static final Foedselsnummer FOEDSELSNUMMER_1 = Foedselsnummer.of("12345678911");
    public static final Foedselsnummer FOEDSELSNUMMER_2 = Foedselsnummer.of("11234567890");
    private static final Foedselsnummer FOEDSELSNUMMER_3 = Foedselsnummer.of("22334455661");
    private static final Foedselsnummer FOEDSELSNUMMER_4 = Foedselsnummer.of("99887766554");

    private static final Bruker BRUKER_1 = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId.of("100002345678"),
            asList(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
    );

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
        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(true);

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 5, 1));

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(FOEDSELSNUMMER_3, forespurtPeriode);

        assertThat(arbeidssokerperiodes).containsExactly(
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_1,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_2,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_4);
    }

    @Test
    public void hentArbeidssokerperioder_skal_hente_fra_ords() {
        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2019, 12, 1),
                LocalDate.of(2020, 5, 1));

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(FOEDSELSNUMMER_3, forespurtPeriode);

        assertThat(arbeidssokerperiodes).containsExactly(
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_0,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_1,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_2,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_3,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_4);
    }

    @Test
    public void hentArbeidssokerperioder_skal_returnere_alle_perioder_for_person_innenfor_forespurt_periode_lokalt() {
        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(true);

        Bruker bruker = Bruker.of(
                FOEDSELSNUMMER_3,
                AktorId.of("100002345678"),
                asList(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
        );

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 3, 20),
                LocalDate.of(2020, 6, 10));

        List<Arbeidssokerperiode> arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(bruker, forespurtPeriode);

        assertThat(arbeidssokerperioder).containsExactly(
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_4,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_5,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_6,
                StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_7
        );

    }

    @Test
    public void hentArbeidssokerperioder_skal_returnere_alle_perioder_for_person_innenfor_forespurt_periode_ORDS() {
        when(unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)).thenReturn(true);

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 5, 9));

        List<Arbeidssokerperiode> arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(BRUKER_1, forespurtPeriode);

        assertThat(arbeidssokerperioder).containsExactly(
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_1,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_2,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_3,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_4,
                StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_5
        );
    }

    private static class StubArbeidssokerRepository implements ArbeidssokerRepository {

        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_1 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_2 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_3 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_4 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_5 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_6 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 10), LocalDate.of(2020, 5, 29)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_7 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 30), LocalDate.of(2020, 6, 30)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_8 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 7, 1), null));

        @Override
        public long lagre(EndretFormidlingsgruppeCommand arenaFormidlingsgruppeEvent) {
            return 0;
        }

        @Override
        public Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
            Map<Foedselsnummer, Arbeidssokerperioder> map = new HashMap<>();

            map.put(FOEDSELSNUMMER_1, new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_2)));

            map.put(FOEDSELSNUMMER_2, new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_6,
                    ARBEIDSSOKERPERIODE_5
            )));

            map.put(FOEDSELSNUMMER_3, new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_7,
                    ARBEIDSSOKERPERIODE_8
            )));

            map.put(FOEDSELSNUMMER_4, new Arbeidssokerperioder(null));

            return map.get(foedselsnummer);
        }

        @Override
        public Arbeidssokerperioder finnFormidlingsgrupper(Bruker bruker) {
            Map<Bruker, Arbeidssokerperioder> map = new HashMap<>();

            map.put(BRUKER_1, new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_2,
                    ARBEIDSSOKERPERIODE_6,
                    ARBEIDSSOKERPERIODE_5,
                    ARBEIDSSOKERPERIODE_7,
                    ARBEIDSSOKERPERIODE_8
            )));

            return map.get(bruker);
        }
    }

    private static class StubFormidlingsgruppeGateway implements FormidlingsgruppeGateway {

        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_0 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 31)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_1 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_2 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_3 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_4 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_5 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9)));
        public static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_6 = new Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 10), null));

        @Override
        public Arbeidssokerperioder finnArbeissokerperioder(Foedselsnummer foedselsnummer, Periode periode) {
            Map<Foedselsnummer, Arbeidssokerperioder> map = new HashMap<>();
            map.put(FOEDSELSNUMMER_3, new Arbeidssokerperioder(asList(
                    ARBEIDSSOKERPERIODE_2,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_0,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_5,
                    ARBEIDSSOKERPERIODE_6
            )));
            return map.get(foedselsnummer);
        }
    }
}
