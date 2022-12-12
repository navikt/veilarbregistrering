package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandTestdataBuilder.registreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
@ActiveProfiles("gcp")
class GcpBrukerRegistreringRepositoryDbIntegrationTest(

    @Autowired
    private val jdbcTemplate: JdbcTemplate,

    @Autowired
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    @Autowired
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    @Autowired
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository
) {
    init {
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @Test
    fun registrerBruker() {
        val registrering = gyldigBrukerRegistrering()
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        assertRegistrertBruker(registrering, ordinaerBrukerRegistrering)
    }

    @Test
    fun `bruker som har registrering og ingen sykmeldtregistrering skal ikke få feil`() {
        val registrering = gyldigBrukerRegistrering(
            besvarelse = BesvarelseTestdataBuilder.gyldigBesvarelse(andreForhold = AndreForholdSvar.NEI)
        )
        brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        assertThat(sykmeldtRegistreringRepository.hentSykmeldtregistreringForAktorId(AKTOR_ID_11111)).isNull()
    }

    private fun assertRegistrertBruker(bruker: OrdinaerBrukerRegistrering, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        assertThat(ordinaerBrukerRegistrering.besvarelse).isEqualTo(bruker.besvarelse)
        assertThat(ordinaerBrukerRegistrering.sisteStilling).isEqualTo(bruker.sisteStilling)
        assertThat(ordinaerBrukerRegistrering.teksterForBesvarelse).isEqualTo(bruker.teksterForBesvarelse)
    }

    @Test
    fun skal_hente_foedselsnummer_tilknyttet_ordinaerBrukerRegistrering() {
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1)
        val bruker = brukerRegistreringRepository.hentBrukerTilknyttet(ordinaerBrukerRegistrering.id)
        assertThat(bruker.gjeldendeFoedselsnummer).isEqualTo(BRUKER_1.gjeldendeFoedselsnummer)
        assertThat(bruker.aktorId).isEqualTo(BRUKER_1.aktorId)
    }

    @Test
    fun `finnOrdinaerBrukerregistreringForAktorIdOgTilstand skal returnere liste med registreringer for angitt tilstand`() {
        val registrering1 = gyldigBrukerRegistrering(besvarelse=(BesvarelseTestdataBuilder.gyldigBesvarelse(
                andreForhold = AndreForholdSvar.JA)))
        val lagretRegistrering1 = brukerRegistreringRepository.lagre(registrering1, BRUKER_1)
        registreringTilstandRepository.lagre(registreringTilstand().brukerRegistreringId(lagretRegistrering1.id).status(Status.OVERFORT_ARENA).build())

        val registrering2 = gyldigBrukerRegistrering(besvarelse=(BesvarelseTestdataBuilder.gyldigBesvarelse(
                andreForhold = AndreForholdSvar.NEI)))
        val lagretRegistrering2 = brukerRegistreringRepository.lagre(registrering2, BRUKER_1)
        registreringTilstandRepository.lagre(registreringTilstand().brukerRegistreringId(lagretRegistrering2.id).status(Status.OVERFORT_ARENA).build())

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(BRUKER_1.aktorId, listOf(Status.OVERFORT_ARENA))
        assertThat(ordinaerBrukerregistreringer).hasSize(2)
    }

    @Test
    fun `finnOrdinaerBrukerregistreringForAktorIdOgTilstand skal returnere tom liste når tilstand ikke finnes`() {
        val registrering1 = gyldigBrukerRegistrering(besvarelse=(BesvarelseTestdataBuilder.gyldigBesvarelse(
                andreForhold = AndreForholdSvar.JA)))
        brukerRegistreringRepository.lagre(registrering1, BRUKER_1)

        val registrering2 = gyldigBrukerRegistrering(besvarelse=(BesvarelseTestdataBuilder.gyldigBesvarelse(
                andreForhold = AndreForholdSvar.NEI)))
        brukerRegistreringRepository.lagre(registrering2, BRUKER_1)

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(BRUKER_1.aktorId, listOf(Status.OVERFORT_ARENA))
        assertThat(ordinaerBrukerregistreringer).isEmpty()
    }

    @Test
    fun `finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer skal returnere AktorId uten Foedselsnummer`() {
        val ordinaerBrukerRegistrering_1 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1)
        val ordinaerBrukerRegistrering_2 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_2)

        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_1.id)
        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_2.id)

        val aktorIdList = brukerRegistreringRepository.finnAktorIdTilRegistrertUtenFoedselsnummer(50)

        assertThat(aktorIdList).hasSize(2)
    }

    @Test
    fun `finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer skal ikke returnere AktorId som er svartelistet`() {
        val ordinaerBrukerRegistrering_1 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1)
        val ordinaerBrukerRegistrering_2 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_2)

        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_1.id)
        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_2.id)

        val aktorIdList = brukerRegistreringRepository.finnAktorIdTilRegistrertUtenFoedselsnummer(50, listOf(BRUKER_2.aktorId))

        assertThat(aktorIdList).hasSize(1)
    }

    @Test
    fun `oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer`() {
        val ordinaerBrukerRegistrering_1 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1)
        val ordinaerBrukerRegistrering_2 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_2)

        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_1.id)
        jdbcTemplate.update("UPDATE BRUKER_REGISTRERING SET FOEDSELSNUMMER = NULL WHERE BRUKER_REGISTRERING_ID = ?", ordinaerBrukerRegistrering_2.id)

        val antallOppdaterteRader =
            brukerRegistreringRepository.oppdaterRegistreringerMedManglendeFoedselsnummer(
                mapOf(BRUKER_1.aktorId to BRUKER_1.gjeldendeFoedselsnummer))

        assertThat(antallOppdaterteRader[0]).isEqualTo(1)
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer("12345678911")
        private val AKTOR_ID_11111 = AktorId("11111")
        private val BRUKER_1 = Bruker(FOEDSELSNUMMER, AKTOR_ID_11111)
        private val BRUKER_2 = Bruker(FoedselsnummerTestdataBuilder.aremark(), AktorId("22222"))
    }
}