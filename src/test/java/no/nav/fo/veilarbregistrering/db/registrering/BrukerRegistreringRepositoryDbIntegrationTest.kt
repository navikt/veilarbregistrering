package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class BrukerRegistreringRepositoryDbIntegrationTest(

    @Autowired
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    @Autowired
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository) {

    @Test
    fun registrerBruker() {
        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        assertRegistrertBruker(registrering, ordinaerBrukerRegistrering)
    }

    @Test
    fun `bruker som har registrering og ingen sykmeldtregistrering skal ikke få feil`() {
        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
            .setAndreForhold(AndreForholdSvar.NEI))
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
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(), BRUKER_1)
        val bruker = brukerRegistreringRepository.hentBrukerTilknyttet(ordinaerBrukerRegistrering.id)
        assertThat(bruker.gjeldendeFoedselsnummer).isEqualTo(BRUKER_1.gjeldendeFoedselsnummer)
        assertThat(bruker.aktorId).isEqualTo(BRUKER_1.aktorId)
    }

    @Test
    fun `finn liste med alle ordinaere registreringer for gitt aktørId`() {
        val registrering1 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA))
        val registrering2 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI))
        brukerRegistreringRepository.lagre(registrering1, BRUKER_1)
        brukerRegistreringRepository.lagre(registrering2, BRUKER_1)

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringerFor(BRUKER_1.aktorId)
        assertThat(ordinaerBrukerregistreringer).hasSize(2)
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer.of("12345678911")
        private val AKTOR_ID_11111 = AktorId.of("11111")
        private val BRUKER_1 = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111)
    }
}