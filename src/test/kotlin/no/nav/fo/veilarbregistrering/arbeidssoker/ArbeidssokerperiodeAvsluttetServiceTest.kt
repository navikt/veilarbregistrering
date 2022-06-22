package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import java.time.LocalDateTime

internal class ArbeidssokerperiodeAvsluttetServiceTest {
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer = mockk(relaxed = true)
    private val arbeidssokerperiodeAvsluttetService = ArbeidssokerperiodeAvsluttetService(arbeidssokerperiodeAvsluttetProducer)

    @BeforeEach
    fun setup() {
        every { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) } just Runs
    }

    @Test
    fun `Skal publisere hendelse når arbeidssokerperiode går fra ARBS til ISERV`() {
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS, NÅVÆRENDE_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify (exactly = 1) { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) }
    }

    @Test
    fun `Skal ikke publisere hendelse når arbeidssokerperiode går fra ARBS til ARBS`() {
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS, NÅVÆRENDE_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ARBS"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 0) { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) }
    }

    @Test
    fun `Skal ikke publisere hendelse når arbeidssokerperiode med ARBS allerede er avsluttet`() {
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 0) { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) }
    }

    @Test
    fun `Skal ikke publisere hendelse når bruker ikke har noen arbeidssøkerperioder fra før`() {
        val arbeidssokerperioder = Arbeidssokerperioder(emptyList())
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 0) { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) }
    }

    private fun endretFormdlingsgruppe(formidlingsgruppe: Formidlingsgruppe): EndretFormidlingsgruppeCommand {
        return object : EndretFormidlingsgruppeCommand {
            override val foedselsnummer = Foedselsnummer("10108000398")
            override val personId = "123456"
            override val personIdStatus = "AKTIV"
            override val operation = Operation.UPDATE
            override val formidlingsgruppe = formidlingsgruppe
            override val formidlingsgruppeEndret = LocalDateTime.now()
            override val forrigeFormidlingsgruppe: Formidlingsgruppe? = null
            override val forrigeFormidlingsgruppeEndret: LocalDateTime? = null
        }
    }

    companion object {
        val AVSLUTTET_ARBS = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        )
        val NÅVÆRENDE_ARBS = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 2, 1), null)
        )
    }
}