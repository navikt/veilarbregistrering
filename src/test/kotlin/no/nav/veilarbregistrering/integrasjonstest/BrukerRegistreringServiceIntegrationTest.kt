package no.nav.veilarbregistrering.integrasjonstest

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.veilarbregistrering.integrasjonstest.BrukerRegistreringServiceIntegrationTest.BrukerregistreringConfigTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.jdbc.JdbcTestUtils
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class, BrukerregistreringConfigTest::class])
internal class BrukerRegistreringServiceIntegrationTest @Autowired constructor(
    private val brukerRegistreringService: BrukerRegistreringService,
    private val oppfolgingGateway: OppfolgingGateway,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    private val profileringRepository: ProfileringRepository,
    private val jdbcTemplate: JdbcTemplate,
) {
    @BeforeEach
    fun setup() {
        clearAllMocks()
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @AfterEach
    fun tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "BRUKER_PROFILERING")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "REGISTRERING_TILSTAND")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "BRUKER_REGISTRERING")
    }

    @Test
    fun `skal ikke rulle tilbake database dersom overforing til arena feiler`() {
        every { oppfolgingGateway.aktiverBruker(any(), any()) } throws RuntimeException()
        val id = brukerRegistreringRepository.lagre(
            OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(),
            BRUKER
        ).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id))
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering())
        assertThrows<RuntimeException> { brukerRegistreringService.overforArena(id, BRUKER, null) }
        val brukerRegistrering = Optional.ofNullable(
            brukerRegistreringRepository.hentBrukerregistreringForId(id)
        )
        val registreringTilstand = registreringTilstandRepository.hentTilstandFor(id)
        Assertions.assertThat(brukerRegistrering).isNotEmpty
        Assertions.assertThat(registreringTilstand.status).isEqualTo(Status.UKJENT_TEKNISK_FEIL)
    }

    @Test
    fun `skal sette registreringsstatus dersom arenafeil er dod eller utvandret`() {
        every { oppfolgingGateway.aktiverBruker(any(), any()) } throws
                AktiverBrukerException("Feil ved aktivering av bruker", AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET)

        val id = brukerRegistreringRepository.lagre(
            OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(),
            BRUKER
        ).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id))
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering())
        assertThrows<AktiverBrukerException> { brukerRegistreringService.overforArena(id, BRUKER, null) }
        val brukerRegistrering = Optional.ofNullable(
            brukerRegistreringRepository.hentBrukerregistreringForId(id)
        )
        val registreringTilstand = registreringTilstandRepository.hentTilstandFor(id)
        Assertions.assertThat(brukerRegistrering).isNotEmpty
        Assertions.assertThat(registreringTilstand.status).isEqualTo(Status.DOD_UTVANDRET_ELLER_FORSVUNNET)
    }

    @Test
    fun skal_sette_registreringsstatus_dersom_arenafeil_er_mangler_opphold() {
        every { oppfolgingGateway.aktiverBruker(any(), any()) } throws
            AktiverBrukerException("Feil ved aktivering av bruker", AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)

        val id = brukerRegistreringRepository.lagre(
            OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(),
            BRUKER
        ).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id))
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering())
        assertThrows<AktiverBrukerException> { brukerRegistreringService.overforArena(id, BRUKER, null) }

        val brukerRegistrering = Optional.ofNullable(
            brukerRegistreringRepository.hentBrukerregistreringForId(id)
        )
        val registreringTilstand = registreringTilstandRepository.hentTilstandFor(id)
        Assertions.assertThat(brukerRegistrering).isNotEmpty
        Assertions.assertThat(registreringTilstand.status).isEqualTo(Status.MANGLER_ARBEIDSTILLATELSE)
    }

    @Test
    fun `gitt at overforing til arena gikk bra skal status oppdateres til overfort arena`() {
        every { oppfolgingGateway.aktiverBruker(any(), any()) } just Runs

        val id = brukerRegistreringRepository.lagre(
            OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(),
            BRUKER
        ).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id))
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering())
        assertDoesNotThrow { brukerRegistreringService.overforArena(id, BRUKER, null) }
        val brukerRegistrering = Optional.ofNullable(
            brukerRegistreringRepository.hentBrukerregistreringForId(id)
        )
        val registreringTilstand = registreringTilstandRepository.hentTilstandFor(id)
        Assertions.assertThat(brukerRegistrering).isNotEmpty
        Assertions.assertThat(registreringTilstand.status).isEqualTo(Status.OVERFORT_ARENA)
    }

    @Configuration
    class BrukerregistreringConfigTest {

        @Bean
        fun oppfolgingGateway(): OppfolgingGateway  = mockk(relaxed = true)

        @Bean
        fun profileringService(): ProfileringService  = mockk(relaxed = true)

        @Bean
        fun hentBrukerTilstandService(oppfolgingGateway: OppfolgingGateway): BrukerTilstandService {
            return BrukerTilstandService(oppfolgingGateway)
        }

        @Bean
        fun metricsService(): MetricsService  = mockk(relaxed = true)

        @Bean
        fun brukerRegistreringService(
            brukerRegistreringRepository: BrukerRegistreringRepository,
            profileringRepository: ProfileringRepository,
            oppfolgingGateway: OppfolgingGateway,
            profileringService: ProfileringService,
            registreringTilstandRepository: RegistreringTilstandRepository,
            brukerTilstandService: BrukerTilstandService,
            manuellRegistreringRepository: ManuellRegistreringRepository,
            metricsService: MetricsService
        ): BrukerRegistreringService {
            return BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                oppfolgingGateway,
                profileringService,
                registreringTilstandRepository,
                brukerTilstandService,
                manuellRegistreringRepository,
                metricsService
            )
        }
    }

    companion object {
        private val BRUKER = Bruker(FoedselsnummerTestdataBuilder.aremark(), AktorId("AKTØRID"))
    }
}