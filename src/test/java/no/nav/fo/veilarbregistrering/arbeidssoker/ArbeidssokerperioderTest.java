package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerperioderTest {

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

    @Test
    public void dersom_forespurt_periode_starter_etter_eldste_periode_dekkes_hele() {

        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_1));

        assertThat(arbeidssokerperioder.dekkerHele(
                Periode.of(
                        LocalDate.of(2020, 2, 1),
                        null))).isTrue();
    }

    @Test
    public void dersom_forespurt_periode_starter_før_eldste_periode_dekkes_ikke_hele() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_1));

        assertThat(arbeidssokerperioder.dekkerHele(
                Periode.of(
                        LocalDate.of(2019, 2, 1),
                        null))).isFalse();
    }

    @Test
    public void dersom_forespurt_periode_starter_samme_dag_som_eldste_periode_dekkes_hele_perioden() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_1));

        assertThat(arbeidssokerperioder.dekkerHele(
                Periode.of(
                        LocalDate.of(2020, 1, 1),
                        null))).isTrue();
    }

}
