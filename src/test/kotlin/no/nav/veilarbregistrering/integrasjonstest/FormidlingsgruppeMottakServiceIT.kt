package no.nav.veilarbregistrering.integrasjonstest

import io.mockk.*
import no.nav.fo.veilarbregistrering.arbeidssoker.*
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.jdbc.JdbcTestUtils
import java.time.LocalDateTime
import kotlin.test.assertEquals

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class, FormidlingsgruppeMottakServiceIT.ArbeidssokerConfigTest::class])
internal class FormidlingsgruppeMottakServiceIT @Autowired constructor(
    private val formidlingsgruppeMottakService: FormidlingsgruppeMottakService,
    private val arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService,
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val jdbcTemplate: JdbcTemplate,
) {
    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "FORMIDLINGSGRUPPE")
    }

    @Test
    fun `skal hente opp eksisterende arbeidssøkerperioder før ny formidlingsgruppe persisteres`() {
        eksisterendeFormidlingsgrupper.map { formidlingsgruppeRepository.lagre(it) }
        val eksisterendeArbeidssokerPerioder = formidlingsgruppeRepository.finnFormidlingsgrupper(listOf(fnr))
        val nyttFormidlingsgruppeEvent = FormidlingsgruppeEvent(
            foedselsnummer = fnr,
            personId = pid,
            personIdStatus = "AKTIV",
            operation = Operation.INSERT,
            formidlingsgruppe = Formidlingsgruppe("ISERV"),
            formidlingsgruppeEndret = third.plusDays(3),
            forrigeFormidlingsgruppe = null,
            forrigeFormidlingsgruppeEndret = null
        )

        val arbeidssokerperioderSlot = slot<Arbeidssokerperioder>()
        every { arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(any(), capture(arbeidssokerperioderSlot)) } just Runs

        formidlingsgruppeMottakService.behandle(nyttFormidlingsgruppeEvent)

        assertEquals(eksisterendeArbeidssokerPerioder, arbeidssokerperioderSlot.captured)
    }

    @Configuration
    class ArbeidssokerConfigTest {

        @Bean
        fun arbeidssokerperiodeAvsluttetService(): ArbeidssokerperiodeAvsluttetService = mockk(relaxed = true)

        @Bean
        fun formidlingsgruppeMottakService(
            formidlingsgruppeRepository: FormidlingsgruppeRepository,
            arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService,
        ): FormidlingsgruppeMottakService = FormidlingsgruppeMottakService(
            formidlingsgruppeRepository,
            arbeidssokerperiodeAvsluttetService
        )

        @Bean
        fun pepClient(): AutorisasjonService = mockk(relaxed = true)
    }

    companion object {
        private const val pid = "41131"
        private val fnr = Foedselsnummer("10067924594")
        private val first = LocalDateTime.of(2020, 5, 1, 3, 5, 1)
        private val second = LocalDateTime.of(2020, 8, 3, 7, 25, 1)
        private val third = LocalDateTime.of(2021, 10, 21, 13, 15, 1)

        private val eksisterendeFormidlingsgrupper = listOf(
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ISERV"),
                first,
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ARBS"),
                first.plusSeconds(1),
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ISERV"),
                second,
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ARBS"),
                third,
                Formidlingsgruppe("ISERV"),
                second
            ),

        )
    }
}