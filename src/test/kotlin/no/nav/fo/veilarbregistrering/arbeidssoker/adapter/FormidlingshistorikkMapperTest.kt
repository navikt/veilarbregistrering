package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class FormidlingshistorikkMapperTest {

    @Test
    fun `skal mappe Formidlingshistorikk med avsluttet periode`() {
        val actualFormidlingshistorikk = FormidlingshistorikkDto(
            "ARBS",
            "2020-02-20 12:03:24",
            LocalDate.of(2020, 1, 12),
            LocalDate.of(2020, 2, 20)
        )

        val expectedArbeidssøkerperiode = Arbeidssokerperiode(
            Formidlingsgruppe("ARBS"),
            Periode(
                LocalDate.of(2020, 1, 12),
                LocalDate.of(2020, 2, 20)
            )
        )
        assertEquals(expectedArbeidssøkerperiode, FormidlingshistorikkMapper.map(actualFormidlingshistorikk))
    }

    @Test
    fun `skal mappe Formidlingshistorikk med åpen periode`() {
        val actualFormidlingshistorikk2 = FormidlingshistorikkDto(
            "ARBS",
            "2020-03-12 14:23:42",
            LocalDate.of(2020, 3, 12),
            null
        )

        val expectedArbeidssøkerperiode2 = Arbeidssokerperiode(
            Formidlingsgruppe("ARBS"),
            Periode(
                LocalDate.of(2020, 3, 12),
                null
            )
        )

        assertEquals(expectedArbeidssøkerperiode2, FormidlingshistorikkMapper.map(actualFormidlingshistorikk2))
    }
}
