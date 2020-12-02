package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status.*
import org.assertj.core.api.Assertions.*
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
        assertThat(result!!).isEqualTo(0)

        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        val initiellTilstand = RegistreringTilstand.medStatus(MOTTATT, ordinaerBrukerRegistrering.id)
        val id: Long = registreringTilstandRepository.lagre(initiellTilstand)

        Profilering()
                .setAlder(42)
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .apply { profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.id, this) }

        Status.values().forEach { status ->
            registreringTilstandRepository.hentRegistreringTilstand(id).oppdaterStatus(status).also { tilstand ->
                registreringTilstandRepository.oppdater(tilstand)
            }

            val finnesIBrukerRegistrering = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING", Int::class.java)!! > 0
            val finnesIBrukerRegistreringTekst = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING_TEKST", Int::class.java)!! > 0
            val finnesIProfilering = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_PROFILERING", Int::class.java)!! > 0

            val expected = skalStatusVisesIView(status)

            assertThat(finnesIBrukerRegistrering).isEqualTo(expected)
            assertThat(finnesIBrukerRegistreringTekst).isEqualTo(expected)
            assertThat(finnesIProfilering).isEqualTo(expected)
        }
    }


    private fun skalStatusVisesIView(status: Status): Boolean =
            when (status) {
                OVERFORT_ARENA, PUBLISERT_KAFKA, OPPRINNELIG_OPPRETTET_UTEN_TILSTAND -> true

                MOTTATT, UKJENT_BRUKER,
                MANGLER_ARBEIDSTILLATELSE, KAN_IKKE_REAKTIVERES,
                DOD_UTVANDRET_ELLER_FORSVUNNET, UKJENT_TEKNISK_FEIL,
                TEKNISK_FEIL -> false
            }
}