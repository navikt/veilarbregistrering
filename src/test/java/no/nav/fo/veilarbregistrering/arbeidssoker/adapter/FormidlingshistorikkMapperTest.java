package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingshistorikkMapperTest {

    @Test
    public void skal_mappe_fra_formidlingsgruppeResponseDto_til_liste_med_arbeidssokerperiode() {
        FormidlingshistorikkDto formidlingshistorikkDto1 = new FormidlingshistorikkDto(
                "ISERV",
                "2019-01-12 12:03:24",
                LocalDate.of(2019, 1, 11),
                LocalDate.of(2020, 1, 12));

        FormidlingshistorikkDto formidlingshistorikkDto2 = new FormidlingshistorikkDto(
                "ARBS",
                "2020-02-20 12:03:24",
                LocalDate.of(2020, 1, 12),
                LocalDate.of(2020, 2, 20));

        FormidlingshistorikkDto formidlingshistorikkDto3 = new FormidlingshistorikkDto(
                "ISERV",
                "2020-03-11 12:03:24",
                LocalDate.of(2020, 2, 21),
                LocalDate.of(2020, 3, 11));

        FormidlingshistorikkDto formidlingshistorikkDto4 = new FormidlingshistorikkDto(
                "ARBS",
                "2020-03-12 14:23:42",
                LocalDate.of(2020, 3, 12),
                null);

        FormidlingsgruppeResponseDto response = new FormidlingsgruppeResponseDto(
                "123456",
                "12345612345",
                asList(
                        formidlingshistorikkDto1,
                        formidlingshistorikkDto2,
                        formidlingshistorikkDto3,
                        formidlingshistorikkDto4));

        List<Arbeidssokerperiode> liste = FormidlingshistorikkMapper.map(response);

        assertThat(liste).hasSize(4);

        assertThat(liste).contains(
                new Arbeidssokerperiode(
                        Formidlingsgruppe.of("ISERV"),
                        Periode.of(
                                LocalDate.of(2020, 2, 21),
                                LocalDate.of(2020, 3, 11))),
                new Arbeidssokerperiode(
                        Formidlingsgruppe.of("ARBS"),
                        Periode.of(
                                LocalDate.of(2020, 3, 12),
                                null)));
    }
}
