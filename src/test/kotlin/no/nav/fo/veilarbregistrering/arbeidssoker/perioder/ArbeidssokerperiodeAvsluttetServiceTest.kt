package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import io.micrometer.core.instrument.Tag
import io.mockk.*
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortService
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class ArbeidssokerperiodeAvsluttetServiceTest {
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer = mockk(relaxed = true)
    private val meldekortService: MeldekortService = mockk(relaxed = true)
    private val metricsService: MetricsService = mockk(relaxed = true)
    private val arbeidssokerperiodeAvsluttetService = ArbeidssokerperiodeAvsluttetService(arbeidssokerperiodeAvsluttetProducer, meldekortService, metricsService)

    @BeforeEach
    fun setup() {
        every { arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(any(), any()) } just Runs
        every { meldekortService.hentSisteMeldekort(any()) } returns null
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
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

    @Test
    fun `Skal ikke publisere event når bruker ikke har meldekort`() {
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS, NÅVÆRENDE_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 0) { metricsService.registrer(any(), *anyVararg<Tag>()) }
    }

    @Test
    fun `Skal publisere event med korrekt tag når bruker svart ja på spørsmål 5`() {
        every { meldekortService.hentSisteMeldekort(any()) } returns MeldekortEvent(
            FoedselsnummerTestdataBuilder.aremark(),
            true,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            1,
            LocalDateTime.now()
        )
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS, NÅVÆRENDE_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 1) { metricsService.registrer(any(), Tag.of("erArbeidssokerNestePeriode", "true")) }
    }

    @Test
    fun `Skal publisere event med korrekt tag når bruker svart nei på spørsmål 5`() {
        every { meldekortService.hentSisteMeldekort(any()) } returns MeldekortEvent(
            FoedselsnummerTestdataBuilder.aremark(),
            false,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            1,
            LocalDateTime.now()
        )
        val arbeidssokerperioder = Arbeidssokerperioder(listOf(AVSLUTTET_ARBS, NÅVÆRENDE_ARBS))
        val formidlingsgruppeEventFraArena = endretFormdlingsgruppe(Formidlingsgruppe("ISERV"))

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(formidlingsgruppeEventFraArena, arbeidssokerperioder)

        verify(exactly = 1) { metricsService.registrer(any(), Tag.of("erArbeidssokerNestePeriode", "false")) }
    }

    private fun endretFormdlingsgruppe(formidlingsgruppe: Formidlingsgruppe): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
            Foedselsnummer("10108000398"),
            "123456",
            "AKTIV",
            Operation.UPDATE,
            formidlingsgruppe,
            LocalDateTime.now(),
            null,
            null
        )
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