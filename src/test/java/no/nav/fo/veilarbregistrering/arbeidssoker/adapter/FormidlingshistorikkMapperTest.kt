package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class FormidlingshistorikkMapperTest {
    @Test
    fun `skal mappe fra formidlingsgruppeResponseDto til liste med arbeidssokerperiode`() {
        val formidlingshistorikkDto1 = FormidlingshistorikkDto(
            "ISERV",
            "2019-01-12 12:03:24",
            LocalDate.of(2019, 1, 11),
            LocalDate.of(2020, 1, 12)
        )
        val formidlingshistorikkDto2 = FormidlingshistorikkDto(
            "ARBS",
            "2020-02-20 12:03:24",
            LocalDate.of(2020, 1, 12),
            LocalDate.of(2020, 2, 20)
        )
        val formidlingshistorikkDto3 = FormidlingshistorikkDto(
            "ISERV",
            "2020-03-11 12:03:24",
            LocalDate.of(2020, 2, 21),
            LocalDate.of(2020, 3, 11)
        )
        val formidlingshistorikkDto4 = FormidlingshistorikkDto(
            "ARBS",
            "2020-03-12 14:23:42",
            LocalDate.of(2020, 3, 12),
            null
        )
        val response = FormidlingsgruppeResponseDto(
            Arrays.asList(
                formidlingshistorikkDto1,
                formidlingshistorikkDto2,
                formidlingshistorikkDto3,
                formidlingshistorikkDto4
            )
        )
        val liste = FormidlingshistorikkMapper.map(response)
        assertThat(liste).hasSize(4)
        assertThat(liste).contains(
            Arbeidssokerperiode(
                Formidlingsgruppe.of("ISERV"),
                Periode(
                    LocalDate.of(2020, 2, 21),
                    LocalDate.of(2020, 3, 11)
                )
            ),
            Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode(
                    LocalDate.of(2020, 3, 12),
                    null
                )
            )
        )
    }
}
