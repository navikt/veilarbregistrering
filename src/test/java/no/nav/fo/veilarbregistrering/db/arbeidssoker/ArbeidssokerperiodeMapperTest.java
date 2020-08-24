package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
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
        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);

        assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isNotNull();
        assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isNotNull();
        assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull();
    }

    @Test
    public void foerste_periode_skal_ha_tildato_lik_dagen_foer_andre_periode_sin_fradato() {
        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);

        assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20));
        assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull();
    }


    @Test
    public void skal_populere_tildato_korrekt_selv_om_listen_kommer_usortert() {
        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);

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
        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(2))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("IARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(4))));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);

        assertThat(arbeidssokerperioder.asList().size()).isEqualTo(1);
        assertThat(arbeidssokerperioder.asList().get(0).getFormidlingsgruppe().stringValue()).isEqualTo("IARBS");
        assertThat(arbeidssokerperioder.asList().get(0).getPeriode().getFra()).isEqualTo(now.toLocalDate());
    }

    @Test
    public void skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag_flere_dager() {
        LocalDateTime now = LocalDateTime.now();
        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(2))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("IARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusSeconds(4))));

        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(7))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(7).plusSeconds(3))));

        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50).plusSeconds(2))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now.plusDays(50).plusSeconds(5))));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);

        assertThat(arbeidssokerperioder.asList().size()).isEqualTo(3);
        assertThat(arbeidssokerperioder.asList().get(0).getFormidlingsgruppe().stringValue()).isEqualTo("IARBS");
        assertThat(arbeidssokerperioder.asList().get(1).getFormidlingsgruppe().stringValue()).isEqualTo("ARBS");
        assertThat(arbeidssokerperioder.asList().get(2).getFormidlingsgruppe().stringValue()).isEqualTo("ISERV");
        assertThat(arbeidssokerperioder.asList().get(0).getPeriode().getFra()).isEqualTo(now.toLocalDate());
        assertThat(arbeidssokerperioder.asList().get(1).getPeriode().getFra()).isEqualTo(now.plusDays(7).toLocalDate());
        assertThat(arbeidssokerperioder.asList().get(2).getPeriode().getFra()).isEqualTo(now.plusDays(50).toLocalDate());
    }

    @Test
    public void skal_flette_to_personer() {

        List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData = new ArrayList<>();
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4397692, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2019, 3, 6, 10, 10))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 9, 11, 10, 10))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 9, 11, 10, 10))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ARBS", 4397692, "AKTIV", Timestamp.valueOf(LocalDateTime.of(2019, 12, 9, 10, 10))));
        arbeidssokerperiodeRaaData.add(new ArbeidssokerperiodeRaaData("ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(LocalDateTime.of(2019, 12, 18, 10, 10))));

        Arbeidssokerperioder arbeidssokerperioder = map(arbeidssokerperiodeRaaData);


    }
}
