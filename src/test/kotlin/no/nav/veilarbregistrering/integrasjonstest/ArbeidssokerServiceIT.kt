package no.nav.veilarbregistrering.integrasjonstest

import io.mockk.clearAllMocks
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.*
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
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
import java.time.LocalDate
import java.time.LocalDateTime

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class, ArbeidssokerServiceIT.ArbeidssokerConfigTest::class])
internal class ArbeidssokerServiceIT @Autowired constructor(
    private val arbeidssokerService: ArbeidssokerService,
    private val arbeidssokerRepository: ArbeidssokerRepository,
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
    fun `Kan hente arbeidssokerperioder uten feil`() {
        commands.map { arbeidssokerRepository.lagre(it) }
        arbeidssokerService.hentArbeidssokerperioder(
            bruker,
            Periode.gyldigPeriode(LocalDate.of(2021, 10, 30), null)
        )
    }

    @Configuration
    class ArbeidssokerConfigTest {

        @Bean
        fun oppfolgingGateway(): OppfolgingGateway = mockk(relaxed = true)

        @Bean
        fun profileringService(): ProfileringService = mockk(relaxed = true)

        @Bean
        fun metricsService(): MetricsService = mockk(relaxed = true)

        @Bean
        fun unleashClient(): UnleashClient = mockk(relaxed = true)

        @Bean
        fun formidlingsgruppeGateway(): FormidlingsgruppeGateway =
            mockk(relaxed = true)

        @Bean
        fun arbeidssokerService(
            arbeidssokerRepository: ArbeidssokerRepository,
            unleashClient: UnleashClient,
            formidlingsgruppeGateway: FormidlingsgruppeGateway,
            metricsService: MetricsService
        ): ArbeidssokerService = ArbeidssokerService(
            arbeidssokerRepository,
            formidlingsgruppeGateway,
            unleashClient,
            metricsService
        )

        @Bean
        fun pepClient(): AutorisasjonService = mockk(relaxed = true)
    }

    companion object {
        private const val pid = "41131"
        private val fnr = Foedselsnummer("10067924594")
        private val bruker = Bruker(fnr, AktorId("123"))
        private val first = LocalDateTime.of(2020, 5, 1, 3, 5, 1)
        private val second = LocalDateTime.of(2020, 8, 3, 7, 25, 1)
        private val third = LocalDateTime.of(2021, 10, 21, 13, 15, 1)

        private val commands = listOf(
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe.IKKE_SERVICEBRUKER,
                first,
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe.ARBEIDSSOKER,
                first.plusSeconds(1),
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe.IKKE_SERVICEBRUKER,
                second,
                null,
                null
            ),
            FormidlingsgruppeEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe.ARBEIDSSOKER,
                third,
                Formidlingsgruppe.IKKE_SERVICEBRUKER,
                second
            ),

        )
    }
}