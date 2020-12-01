package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.springframework.jdbc.core.JdbcTemplate

class CreateViewTest : DbIntegrasjonsTest() {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var registreringTilstandRepository: RegistreringTilstandRepository
    private lateinit var profileringRepository: ProfileringRepository

    private val FOEDSELSNUMMER = Foedselsnummer.of("12345678911")
    private val AKTOR_ID_11111 = AktorId.of("11111")
    private val BRUKER_1 = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111)

    @Before
    fun setup() {
        jdbcTemplate = getBean(JdbcTemplate::class.java)
        brukerRegistreringRepository = BrukerRegistreringRepositoryImpl(jdbcTemplate)
        registreringTilstandRepository = RegistreringTilstandRepositoryImpl(jdbcTemplate)
        profileringRepository = ProfileringRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `Riktig status er hensyntatt i viewene`() {
        val result = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING", Int::class.java)
        Assertions.assertThat(result!!).isEqualTo(0)

        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        val initiellTilstand = RegistreringTilstand.medStatus(Status.MOTTATT, ordinaerBrukerRegistrering.id)
        val id: Long = registreringTilstandRepository.lagre(initiellTilstand)

        Profilering()
                .setAlder(42)
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .apply { profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.id, this) }

        Status.values().forEach {
            val tilstand = registreringTilstandRepository.hentRegistreringTilstand(id).oppdaterStatus(it)
            registreringTilstandRepository.oppdater(tilstand)

            val finnesIBrukerRegistrering = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING", Int::class.java)!! > 0
            val finnesIBrukerRegistreringTekst = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING_TEKST", Int::class.java)!! > 0
            val finnesIProfilering = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_PROFILERING", Int::class.java)!! > 0

            val expected = skalStatusVisesIView(it)

            Assertions.assertThat(finnesIBrukerRegistrering).isEqualTo(expected)
            Assertions.assertThat(finnesIBrukerRegistreringTekst).isEqualTo(expected)
            Assertions.assertThat(finnesIProfilering).isEqualTo(expected)
        }
    }


    fun skalStatusVisesIView(status: Status): Boolean =
            when (status) {
                Status.OVERFORT_ARENA, Status.PUBLISERT_KAFKA, Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND -> true

                Status.MOTTATT, Status.ARENA_OK, Status.UKJENT_BRUKER,
                Status.MANGLER_ARBEIDSTILLATELSE, Status.KAN_IKKE_REAKTIVERES,
                Status.DOD_UTVANDRET_ELLER_FORSVUNNET, Status.UKJENT_TEKNISK_FEIL,
                Status.TEKNISK_FEIL, Status.OPPGAVE_OPPRETTET, Status.OPPGAVE_FEILET -> false
            }
}