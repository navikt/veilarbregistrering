package no.nav.fo.veilarbregistrering.registrering.ordinaer.db

import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandTestdataBuilder.registreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.JobbsituasjonBeskrivelse
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class BrukerRegistreringRepositoryDbIntegrationTest(

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

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
            BRUKER_1.aktorId, listOf(Status.OVERFORT_ARENA))
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

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
            BRUKER_1.aktorId, listOf(Status.OVERFORT_ARENA))
        assertThat(ordinaerBrukerregistreringer).isEmpty()
    }

    @Test
    fun `skal kunne hente brukerregistering på en liste av fødselsnummer`() {
        val registrering1 = gyldigBrukerRegistrering(opprettetDato = LocalDate.now().atStartOfDay().minusMonths(3))
        val lagretRegistrering1 = brukerRegistreringRepository.lagre(registrering1, BRUKER_1)
        registreringTilstandRepository.lagre(registreringTilstand().brukerRegistreringId(lagretRegistrering1.id).status(Status.OVERFORT_ARENA).build())

        val ordinaerBrukerRegistrerings = brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(BRUKER_1.alleFoedselsnummer())

        assertThat(ordinaerBrukerRegistrerings.size).isEqualTo(1)
    }

    @Test
    fun `skal ikke returnere registreringer som har feilet ved overføring til Arena`() {
        val registrering1 = gyldigBrukerRegistrering(opprettetDato = LocalDate.now().atStartOfDay().minusMonths(3))
        val lagretRegistrering1 = brukerRegistreringRepository.lagre(registrering1, BRUKER_1)
        registreringTilstandRepository.lagre(registreringTilstand().brukerRegistreringId(lagretRegistrering1.id).status(Status.MANGLER_ARBEIDSTILLATELSE).build())

        val ordinaerBrukerRegistrerings = brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(BRUKER_1.alleFoedselsnummer())

        assertThat(ordinaerBrukerRegistrerings.isEmpty()).isTrue()
    }

    @Test
    fun skal_hente_ordinaerBrukerRegistrering() {
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1)
        val liste = brukerRegistreringRepository.hentNesteOpplysningerOmArbeidssoeker(1)
        assertThat(liste).hasSize(1)
        val (id, opplysninger) = liste.first()

        assertThat(id).isEqualTo(ordinaerBrukerRegistrering.id)
        assertThat(opplysninger.identitetsnummer).isEqualTo(BRUKER_1.gjeldendeFoedselsnummer.foedselsnummer)
        assertThat(opplysninger.opplysningerOmArbeidssoeker.metadata.utfoertAv.id).isEqualTo(BRUKER_1.gjeldendeFoedselsnummer.foedselsnummer)
        assertThat(opplysninger.opplysningerOmArbeidssoeker.jobbsituasjon.beskrivelser).hasSize(1)
        assertThat(opplysninger.opplysningerOmArbeidssoeker.jobbsituasjon.beskrivelser.first().beskrivelse).isEqualTo(JobbsituasjonBeskrivelse.IKKE_VAERT_I_JOBB_SISTE_2_AAR)
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer("12345678911")
        private val AKTOR_ID_11111 = AktorId("11111")
        private val BRUKER_1 = Bruker(FOEDSELSNUMMER, AKTOR_ID_11111)
    }
}