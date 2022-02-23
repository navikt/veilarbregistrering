package no.nav.fo.veilarbregistrering.db

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.enhet.Kommune.KommuneMedBydel.STAVANGER
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

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [RepositoryConfig::class, DatabaseConfig::class, HentBrukerRegistreringServiceIntegrationTest.Companion.TestContext::class])
class HentBrukerRegistreringServiceIntegrationTest(
    @Autowired val brukerRegistreringRepository: BrukerRegistreringRepository,
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
                    manuellRegistreringRepository: ManuellRegistreringRepository
            ) = HentRegistreringService(
                    brukerRegistreringRepository,
                    sykmeldtRegistreringRepository,
                    profileringRepository,
                    manuellRegistreringRepository,
                    norg2Gateway()
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
            fun oppfolgingGateway(): OppfolgingGateway = mockk()

            @Bean
            fun profileringService(): ProfileringService = mockk()

            @Bean
            fun norg2Gateway() = object : Norg2Gateway {
                override fun hentEnhetFor(kommune: Kommune): Enhetnr? {
                    if (Kommune("1241") == kommune) {
                        return Enhetnr("232")
                    }
                    return if (Kommune.medBydel(STAVANGER) == kommune) {
                        Enhetnr("1103")
                    } else null
                }

                override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = emptyMap()
            }
        }
    }
}
