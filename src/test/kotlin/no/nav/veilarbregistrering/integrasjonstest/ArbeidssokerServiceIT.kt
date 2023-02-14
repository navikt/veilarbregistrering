package no.nav.veilarbregistrering.integrasjonstest

import io.mockk.clearAllMocks
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerService
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.PopulerArbeidssokerperioderService
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
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
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val reaktiveringRepository: ReaktiveringRepository,
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
        eksisterendeFormidlingsgrupper.map { formidlingsgruppeRepository.lagre(it) }
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
            formidlingsgruppeGateway: FormidlingsgruppeGateway,
            unleashClient: UnleashClient,
            metricsService: MetricsService,
            formidlingsgruppeRepository: FormidlingsgruppeRepository,
            brukerRegistreringRepository: BrukerRegistreringRepository,
            reaktiveringRepository: ReaktiveringRepository
        ): ArbeidssokerService = ArbeidssokerService(
            formidlingsgruppeGateway,
            PopulerArbeidssokerperioderService(
                formidlingsgruppeRepository,
                brukerRegistreringRepository,
                reaktiveringRepository),
            unleashClient,
            metricsService
        )
    }

    companion object {
        private const val pid = "41131"
        private val fnr = Foedselsnummer("10067924594")
        private val bruker = Bruker(fnr, AktorId("123"))
        private val first = LocalDateTime.of(2020, 5, 1, 3, 5, 1)
        private val second = LocalDateTime.of(2020, 8, 3, 7, 25, 1)
        private val third = LocalDateTime.of(2021, 10, 21, 13, 15, 1)

        private val eksisterendeFormidlingsgrupper = listOf(
            FormidlingsgruppeEndretEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ISERV"),
                first,
                null,
                null
            ),
            FormidlingsgruppeEndretEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ARBS"),
                first.plusSeconds(1),
                null,
                null
            ),
            FormidlingsgruppeEndretEvent(
                fnr,
                pid,
                "AKTIV",
                Operation.INSERT,
                Formidlingsgruppe("ISERV"),
                second,
                null,
                null
            ),
            FormidlingsgruppeEndretEvent(
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