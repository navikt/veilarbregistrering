package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder.medArbs;
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder.medIserv;
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperioderTestdataBuilder.arbeidssokerperioder;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerperioderTest {

    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_1 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ISERV"),
            Periode.of(LocalDate.of(2016, 9, 24), null));
    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_2 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ARBS"),
            Periode.of(LocalDate.of(2020, 1, 1), null));
    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_3 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ARBS"),
            Periode.of(LocalDate.of(2020, 2, 1), null));
    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_4 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ISERV"),
            Periode.of(LocalDate.of(2020, 3, 1), null));
    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_5 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ARBS"),
            Periode.of(LocalDate.of(2020, 4, 1), null));
    private static final Arbeidssokerperiode ARBEIDSSOKERPERIODE_6 = new Arbeidssokerperiode(
            Formidlingsgruppe.of("ARBS"),
            Periode.of(LocalDate.of(2020, 6, 9), null));

    @Test
    public void gitt_at_forespurt_periode_starter_etter_eldste_periode_dekkes_hele() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_2));

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 2, 1),
                null);

        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue();
    }

    @Test
    public void gitt_at_forespurt_periode_starter_før_eldste_periode_dekkes_ikke_hele() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_2));

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2019, 2, 1),
                null);

        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isFalse();
    }

    @Test
    public void gitt_at_forespurt_periode_starter_samme_dag_som_eldste_periode_dekkes_hele_perioden() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_2));

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2020, 1, 1),
                null);

        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue();
    }

    @Test
    public void gitt_at_forespurt_periode_slutter_dagen_etter_siste_periode() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_1));

        Periode forespurtPeriode = Periode.of(
                LocalDate.of(2016, 10, 1),
                LocalDate.of(2020, 6, 25));

        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue();
    }

    @Test
    public void gitt_flere_perioder_skal_de_periodene_hvor_en_er_arbs_returneres() {

        Arbeidssokerperioder arbeidssokerperioder = arbeidssokerperioder()
                .periode(medArbs()
                        .fra(LocalDate.of(2020, 3, 19))
                        .til(LocalDate.of(2020, 4, 20)))
                .periode(medIserv()
                        .fra(LocalDate.of(2020, 4, 21))
                        .til(LocalDate.of(2020, 4, 29)))
                .periode(medArbs()
                        .fra(LocalDate.of(2020, 4, 30)))
                .build();

        List<Arbeidssokerperiode> arbeidssokerperiodes = arbeidssokerperioder.overlapperMed(
                Periode.of(
                        LocalDate.of(2020, 4, 13),
                        LocalDate.of(2020, 6, 28)));

        assertThat(arbeidssokerperiodes).hasSize(2);
    }

    @Test
    public void skal_slaa_sammen_Arbeidssokerperioder_korrekt() {

        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_6,
                ARBEIDSSOKERPERIODE_5,
                ARBEIDSSOKERPERIODE_4
        ));

        List<Arbeidssokerperioder> andreArbeidssokerperioder = asList(
                new Arbeidssokerperioder(asList(ARBEIDSSOKERPERIODE_1)),
                new Arbeidssokerperioder(asList(ARBEIDSSOKERPERIODE_2, ARBEIDSSOKERPERIODE_3))
        );

        Arbeidssokerperioder alleArbeidssokerperioder = arbeidssokerperioder.slaaSammenMed(andreArbeidssokerperioder);

        assertThat(alleArbeidssokerperioder.asList()).containsExactly(
                ARBEIDSSOKERPERIODE_1,
                ARBEIDSSOKERPERIODE_2,
                ARBEIDSSOKERPERIODE_3,
                ARBEIDSSOKERPERIODE_4,
                ARBEIDSSOKERPERIODE_5,
                ARBEIDSSOKERPERIODE_6
        );
    }

    @Test
    public void skal_forsoke_aa_slaa_sammen_Arbeidssokerperioder_selv_om_argument_er_tomt() {
        Arbeidssokerperioder arbeidssokerperioder = new Arbeidssokerperioder(Arrays.asList(
                ARBEIDSSOKERPERIODE_6,
                ARBEIDSSOKERPERIODE_5,
                ARBEIDSSOKERPERIODE_4
        ));

        List<Arbeidssokerperioder> andreArbeidssokerperioder = asList(
                new Arbeidssokerperioder(null)
        );

        Arbeidssokerperioder alleArbeidssokerperioder = arbeidssokerperioder.slaaSammenMed(andreArbeidssokerperioder);

        assertThat(alleArbeidssokerperioder.asList()).containsExactly(
                ARBEIDSSOKERPERIODE_4,
                ARBEIDSSOKERPERIODE_5,
                ARBEIDSSOKERPERIODE_6
        );
    }
}
