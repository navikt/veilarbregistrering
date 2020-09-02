package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperioderMapper.map;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerperiodeMapperTest {

    @Test
    public void kun_siste_periode_kan_ha_blank_tildato() {
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isNotNull();
        assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isNotNull();
        assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull();
    }

    @Test
    public void foerste_periode_skal_ha_tildato_lik_dagen_foer_andre_periode_sin_fradato() {
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20));
        assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull();
    }


    @Test
    public void skal_populere_tildato_korrekt_selv_om_listen_kommer_usortert() {
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        assertThat(funnetFraDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 3, 19));
        assertThat(funnetFraDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 21));
        assertThat(funnetFraDatoForIndeks(2, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 5, 30));

        assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20));
        assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 5, 29));
        assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull();

    }

    private LocalDate funnetFraDatoForIndeks(int indeks, Arbeidssokerperioder arbeidssokerperioder) {
        return arbeidssokerperioder.asList().get(indeks).getPeriode().getFra();
    }

    private LocalDate funnetTilDatoForSistePeriode(Arbeidssokerperioder arbeidssokerperioder) {
        return arbeidssokerperioder.asList().get(arbeidssokerperioder.asList().size()-1).getPeriode().getTil();
    }

    private LocalDate funnetTilDatoForIndeks(int indeks, Arbeidssokerperioder arbeidssokerperioder) {
        return arbeidssokerperioder.asList().get(indeks).getPeriode().getTil();
    }


    @Test
    public void skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag() {
        LocalDateTime now = LocalDateTime.now();
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(2))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("IARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(4))));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        assertThat(arbeidssokerperioder.asList().size()).isEqualTo(1);
        assertThat(arbeidssokerperioder.asList().get(0).getFormidlingsgruppe().stringValue()).isEqualTo("IARBS");
        assertThat(arbeidssokerperioder.asList().get(0).getPeriode().getFra()).isEqualTo(now.toLocalDate());
    }

    @Test
    public void skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag_flere_dager() {
        LocalDateTime now = LocalDateTime.now();
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(2))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("IARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(4))));

        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(7))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(7).plusSeconds(3))));

        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50).plusSeconds(2))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50).plusSeconds(5))));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        assertThat(arbeidssokerperioder.asList().size()).isEqualTo(3);
        assertThat(arbeidssokerperioder.asList().get(0).getFormidlingsgruppe().stringValue()).isEqualTo("IARBS");
        assertThat(arbeidssokerperioder.asList().get(1).getFormidlingsgruppe().stringValue()).isEqualTo("ARBS");
        assertThat(arbeidssokerperioder.asList().get(2).getFormidlingsgruppe().stringValue()).isEqualTo("ISERV");
        assertThat(arbeidssokerperioder.asList().get(0).getPeriode().getFra()).isEqualTo(now.toLocalDate());
        assertThat(arbeidssokerperioder.asList().get(1).getPeriode().getFra()).isEqualTo(now.plusDays(7).toLocalDate());
        assertThat(arbeidssokerperioder.asList().get(2).getPeriode().getFra()).isEqualTo(now.plusDays(50).toLocalDate());
    }

    @Test
    public void skal_filtrere_bort_endringer_for_duplikate_identer() {

        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2019, 3, 6, 10, 10))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 9, 11, 10, 10))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 9, 11, 10, 10))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2019, 12, 9, 10, 10))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 12, 18, 10, 10))));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        Arbeidssokerperiode iservAktiv = Arbeidssokerperiode.of(Formidlingsgruppe.of("ISERV"), Periode.of(LocalDate.of(2019, 3, 6), LocalDate.of(2019, 12, 8)));
        Arbeidssokerperiode arbsAktiv = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.of(2019, 12, 9), null));

        assertThat(arbeidssokerperioder.asList()).containsExactly(
                iservAktiv,
                arbsAktiv
        );
    }

    @Test
    public void skal_filtrere_bort_tekniske_ISERVendringer() {
        List<Formidlingsgruppeendring> formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 8, 14, 22, 7, 15))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 8, 14, 22, 7, 15))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 9, 9, 9, 9, 9))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 9, 9, 9, 9, 9))));

        Arbeidssokerperioder arbeidssokerperioder = map(formidlingsgruppeendringer);

        Arbeidssokerperiode arbs1 = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 9, 8)));
        Arbeidssokerperiode arbs2 = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.of(2020, 9, 9), null));

        assertThat(arbeidssokerperioder.asList()).containsExactly(
                arbs1,
                arbs2
        );

        formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 8, 14, 22, 7, 15))));
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ISERV", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 8, 14, 22, 7, 15))));

        arbeidssokerperioder = map(formidlingsgruppeendringer);

        arbs1 = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.of(2020, 8, 14), null));

        assertThat(arbeidssokerperioder.asList()).containsExactly(
                arbs1
        );

        formidlingsgruppeendringer = new ArrayList<>();
        formidlingsgruppeendringer.add(new Formidlingsgruppeendring("ARBS", 4685858, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2020, 8, 14, 22, 7, 15))));

        arbeidssokerperioder = map(formidlingsgruppeendringer);

        arbs1 = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.of(2020, 8, 14), null));

        assertThat(arbeidssokerperioder.asList()).containsExactly(
                arbs1
        );
    }
}
