package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.besvarelse.Stilling
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@TransactionalTest
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class, HentBrukerRegistreringServiceIntegrationTest.Companion.TestContext::class])
open class HentBrukerRegistreringServiceIntegrationTest(
        @Autowired val brukerRegistreringRepository: BrukerRegistreringRepository,
        @Autowired val registreringTilstandRepository: RegistreringTilstandRepository,
        @Autowired val hentRegistreringService: HentRegistreringService,
        @Autowired var oppfolgingGateway: OppfolgingGateway,
        @Autowired var profileringService: ProfileringService,
        @Autowired val jdbcTemplate: JdbcTemplate
) {

    @BeforeEach
    fun setupEach() {
        MigrationUtils.createTables(jdbcTemplate)
        `when`(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(Oppfolgingsstatus(false, false, null, null, null, null));
        `when`(profileringService.profilerBruker(anyInt(), any(), any())).thenReturn(lagProfilering());
    }

    @Test
    fun `henter opp siste brukerregistrering med filtre på tilstand`() {
        brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id.let { id ->
            registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
        }
        brukerRegistreringRepository.lagre(BRUKER_UTEN_JOBB, BRUKER).id.let { id ->
            registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, id))
        }
        assertEquals(hentRegistreringService.hentOrdinaerBrukerRegistrering(BRUKER).sisteStilling, gyldigStilling())
    }

    companion object {
        private val ident = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
        private val BRUKER = Bruker.of(ident, AktorId.of("AKTØRID"))
        private val BRUKER_UTEN_JOBB = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb().setOpprettetDato(LocalDate.of(2014, 12, 8).atStartOfDay())
        private val SELVGAENDE_BRUKER = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setOpprettetDato(LocalDate.of(2018, 12, 8).atStartOfDay())

        @Configuration
        open class TestContext {
            @Bean
            open fun hentRegistreringService(
                    db: JdbcTemplate,
                    brukerRegistreringRepository: BrukerRegistreringRepository,
                    profileringRepository: ProfileringRepository) = HentRegistreringService(brukerRegistreringRepository, profileringRepository, manuellRegistreringService(db))

            @Bean
            open fun hentBrukerTilstandService(oppfolgingGateway: OppfolgingGateway?, sykemeldingService: SykemeldingService?): BrukerTilstandService? {
                return BrukerTilstandService(oppfolgingGateway, sykemeldingService)
            }

            @Bean
            open fun sykemeldingService(): SykemeldingService = Mockito.mock(SykemeldingService::class.java)

            @Bean
            open fun oppfolgingGateway(): OppfolgingGateway = Mockito.mock(OppfolgingGateway::class.java)

            @Bean
            open fun profileringService(): ProfileringService = Mockito.mock(ProfileringService::class.java)

            @Bean
            open fun manuellRegistreringService(db: JdbcTemplate) = ManuellRegistreringService(manuellRegistreringRepository(db), norg2Gateway())

            @Bean
            open fun manuellRegistreringRepository(db: JdbcTemplate): ManuellRegistreringRepository = ManuellRegistreringRepositoryImpl(db)

            @Bean
            open fun norg2Gateway() = object : Norg2Gateway {
                override fun hentEnhetFor(kommunenummer: Kommunenummer): Optional<Enhetnr> {
                    if (Kommunenummer.of("1241") == kommunenummer) {
                        return Optional.of(Enhetnr.of("232"))
                    }
                    return if (Kommunenummer.of(Kommunenummer.KommuneMedBydel.STAVANGER) == kommunenummer) {
                        Optional.of(Enhetnr.of("1103"))
                    } else Optional.empty()
                }

                override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = emptyMap()

            }
        }
    }
}
