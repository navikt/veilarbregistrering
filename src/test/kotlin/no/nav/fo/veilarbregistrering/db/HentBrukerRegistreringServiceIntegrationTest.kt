package no.nav.fo.veilarbregistrering.db

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.enhet.Kommune.KommuneMedBydel.STAVANGER
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import kotlin.test.assertNull

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [RepositoryConfig::class, DatabaseConfig::class, HentBrukerRegistreringServiceIntegrationTest.Companion.TestContext::class])
class HentBrukerRegistreringServiceIntegrationTest(
    @Autowired val brukerRegistreringRepository: BrukerRegistreringRepository,
    @Autowired val manuellRegistreringRepository: ManuellRegistreringRepository,
    @Autowired val registreringTilstandRepository: RegistreringTilstandRepository,
    @Autowired val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    @Autowired val hentRegistreringService: HentRegistreringService,
    @Autowired var oppfolgingGateway: OppfolgingGateway,
    @Autowired var profileringService: ProfileringService,
    @Autowired val profileringRepository: ProfileringRepository,
) {

    @BeforeEach
    fun setupEach() {
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns Oppfolgingsstatus(
            isUnderOppfolging = false,
            kanReaktiveres = false,
            erSykmeldtMedArbeidsgiver = null,
            formidlingsgruppe = null,
            servicegruppe = null,
            rettighetsgruppe = null
        )
        every { profileringService.profilerBruker(any(), any(), any()) } returns lagProfilering()
    }

    @Test
    fun `henter opp siste brukerregistrering med filtre på tilstand`() {
        brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id.let { id ->
            registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
            profileringRepository.lagreProfilering(id, lagProfilering())
        }
        brukerRegistreringRepository.lagre(BRUKER_UTEN_JOBB, BRUKER).id.let { id ->
            registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
            profileringRepository.lagreProfilering(id, lagProfilering())

        }
        assertEquals(gyldigStilling(), hentRegistreringService.hentOrdinaerBrukerRegistrering(BRUKER)?.sisteStilling)
    }

    @Test
    fun `henter opp siste brukerregistrering riktig`() {
        brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id.let { id ->
            registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
            profileringRepository.lagreProfilering(id, lagProfilering())
        }
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(SYKMELDT_BRUKER, BRUKER.aktorId)
        assertEquals(BrukerRegistreringType.SYKMELDT, hentRegistreringService.hentBrukerregistrering(BRUKER)?.type )
    }

    @Test
    fun `hent sykmeldt med veileders enhet med navn`() {
        val id = sykmeldtRegistreringRepository.lagreSykmeldtBruker(SYKMELDT_BRUKER, BRUKER.aktorId)
        val manuellRegistrering = ManuellRegistrering(id, BrukerRegistreringType.SYKMELDT, "H114522", "0106")
        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering)

        val brukerRegistreringWrapper = hentRegistreringService.hentBrukerregistrering(BRUKER)
        assertEquals(BrukerRegistreringType.SYKMELDT, brukerRegistreringWrapper?.type)
        assertEquals("NAV Fredrikstad", brukerRegistreringWrapper?.registrering?.manueltRegistrertAv?.enhet?.navn)
    }

    @Test
    fun `hent ordinær registrering med veileders enhet med navn`() {
        val id = brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
        profileringRepository.lagreProfilering(id, lagProfilering())

        val manuellRegistrering = ManuellRegistrering(id, BrukerRegistreringType.ORDINAER, "H114522", "0106")
        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering)

        val brukerRegistreringWrapper = hentRegistreringService.hentBrukerregistrering(BRUKER)

        assertEquals(BrukerRegistreringType.ORDINAER, brukerRegistreringWrapper?.type)
        assertEquals("NAV Fredrikstad", brukerRegistreringWrapper?.registrering?.manueltRegistrertAv?.enhet?.navn)
    }

    @Test
    fun `hent ordinær registrering uten veileder`() {
        val id = brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
        profileringRepository.lagreProfilering(id, lagProfilering())

        val brukerRegistreringWrapper = hentRegistreringService.hentBrukerregistrering(BRUKER)

        assertEquals(BrukerRegistreringType.ORDINAER, brukerRegistreringWrapper?.type)
        assertNull(brukerRegistreringWrapper?.registrering?.manueltRegistrertAv)
    }

    companion object {
        private val ident = Foedselsnummer("10108000398") //Aremark fiktivt fnr.";
        private val BRUKER = Bruker(ident, AktorId("AKTØRID"))
        private val SYKMELDT_BRUKER = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(
            opprettetDato = LocalDate.of(2019,10,10).atStartOfDay()
        )
        private val BRUKER_UTEN_JOBB = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb(opprettetDato =
            LocalDate.of(2014, 12, 8).atStartOfDay())
        private val SELVGAENDE_BRUKER = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            opprettetDato = LocalDate.of(2018, 12, 8).atStartOfDay()
        )

        @Configuration
        class TestContext {
            @Bean
            fun hentRegistreringService(
                    db: JdbcTemplate,
                    brukerRegistreringRepository: BrukerRegistreringRepository,
                    sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
                    profileringRepository: ProfileringRepository,
                    manuellRegistreringRepository: ManuellRegistreringRepository,
                    metricsService: MetricsService
            ) = HentRegistreringService(
                    brukerRegistreringRepository,
                    sykmeldtRegistreringRepository,
                    profileringRepository,
                    manuellRegistreringRepository,
                    norg2Gateway(),
                    metricsService
            )

            @Bean
            fun hentBrukerTilstandService(
                    oppfolgingGateway: OppfolgingGateway,
                    brukerRegistreringRepository: BrukerRegistreringRepository,
            ): BrukerTilstandService {
                return BrukerTilstandService(
                    oppfolgingGateway,
                    brukerRegistreringRepository
                )
            }

            @Bean
            fun metricsService(): MetricsService = mockk(relaxed = true)

            @Bean
            fun oppfolgingGateway(): OppfolgingGateway = mockk()

            @Bean
            fun profileringService(): ProfileringService = mockk()

            @Bean
            fun norg2Gateway() = object : Norg2Gateway {
                override fun hentEnhetFor(kommune: Kommune): Enhetnr? {
                    if (Kommune("1241") == kommune) {
                        return Enhetnr("0106")
                    }
                    return if (Kommune.medBydel(STAVANGER) == kommune) {
                        Enhetnr("0232")
                    } else null
                }

                override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = mapOf(
                    Enhetnr("0106") to NavEnhet("0106", "NAV Fredrikstad"),
                    Enhetnr("0232") to NavEnhet("0232", "Stavanger")
                )
            }
        }
    }
}
