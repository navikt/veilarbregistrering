package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringTestdataBuilder
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import java.time.LocalDateTime

class PopulerArbeidssokerperioderServiceTest {

    private lateinit var populerArbeidssokerperioderService: PopulerArbeidssokerperioderService

    @BeforeEach
    fun setup() {
        val formidlingsgruppeRepository: FormidlingsgruppeRepository = mockk()
        val brukerRegistreringRepository: BrukerRegistreringRepository = mockk()
        val reaktiveringRepository: ReaktiveringRepository = mockk()
        populerArbeidssokerperioderService = PopulerArbeidssokerperioderService(
            formidlingsgruppeRepository,
            brukerRegistreringRepository,
            reaktiveringRepository
        )

        every { formidlingsgruppeRepository.finnFormidlingsgruppeEndretEventFor(any()) } returns formidlingsgrupper
        every { brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(any()) } returns listOf(
            registrering
        )
        every { reaktiveringRepository.finnReaktiveringerForFoedselsnummer(any()) } returns listOf(reaktivering)
    }

    @Test
    fun `skal populere arbeidssøker med formidlingsgrupper, registrering og reaktivering`() {
        val arbeidssoker = populerArbeidssokerperioderService.hentArbeidssøker(
            Bruker(
                FOEDSELSNUMMER,
                AKTOR_ID,
                HISTORISKE_FOEDSELSNUMMER
            )
        )

        assertEquals(3, arbeidssoker.allePerioder().size)
        assertEquals(LocalDate.of(2015, 5, 15), arbeidssoker.allePerioder().first().fraDato.toLocalDate())
        assertEquals(LocalDate.of(2018, 6, 14), arbeidssoker.allePerioder().first().tilDato?.toLocalDate())
        assertEquals(LocalDate.of(2019, 3, 12), arbeidssoker.allePerioder()[1].fraDato.toLocalDate())
        assertEquals(LocalDate.of(2022, 12, 10), arbeidssoker.allePerioder()[1].tilDato?.toLocalDate())
        assertEquals(LocalDate.of(2023, 1, 1), arbeidssoker.sistePeriode()?.fraDato?.toLocalDate())
        assertNull(arbeidssoker.sistePeriode()?.tilDato)
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer("12345678910")
        private val HISTORISKE_FOEDSELSNUMMER = listOf(Foedselsnummer("12345678911"))
        private val AKTOR_ID = AktorId("1234")

        private val formidlingsgrupper = listOf(
            FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret(
                tidspunkt = LocalDateTime.of(2015, 5, 15, 10, 5, 6),
                formidlingsgruppe = "ISERV"
            ),
            FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret(
                tidspunkt = LocalDateTime.of(2015, 5, 15, 10, 5, 35),
                formidlingsgruppe = "ARBS"
            ),
            FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret(
                tidspunkt = LocalDateTime.of(2018, 6, 14, 10, 5, 35),
                formidlingsgruppe = "ISERV"
            ),
            FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret(
                tidspunkt = LocalDateTime.of(2022, 12, 10, 10, 5, 35),
                formidlingsgruppe = "ISERV",
                operation = Operation.UPDATE
            )
        )
        private val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            opprettetDato = LocalDateTime.of(2019, 3, 12, 11, 30, 2)
        )

        private val reaktivering = ReaktiveringTestdataBuilder.gyldigReaktivering(
            aktorId = AKTOR_ID,
            opprettetDato = LocalDateTime.of(2023, 1, 1, 14, 35, 20)
        )
    }
}