package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.formidling.Status.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class CreateViewTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    @Autowired
    private lateinit var registreringTilstandRepository: RegistreringTilstandRepository
    @Autowired
    private lateinit var profileringRepository: ProfileringRepository

    private val FOEDSELSNUMMER = Foedselsnummer("12345678911")
    private val AKTOR_ID_11111 = AktorId("11111")
    private val BRUKER_1 = Bruker(FOEDSELSNUMMER, AKTOR_ID_11111)

    @Test
    fun `Riktig status er hensyntatt i viewene`() {
        val result = jdbcTemplate.queryForObject("SELECT count(*) FROM DVH_BRUKER_REGISTRERING", Int::class.java)
        assertThat(result!!).isEqualTo(0)

        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1)
        val initiellTilstand = RegistreringTilstand.medStatus(MOTTATT, ordinaerBrukerRegistrering.id)
        val id: Long = registreringTilstandRepository.lagre(initiellTilstand)

        Profilering(Innsatsgruppe.STANDARD_INNSATS, 42, true)
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

                MOTTATT, UKJENT_BRUKER, MANGLER_ARBEIDSTILLATELSE,
                KAN_IKKE_REAKTIVERES, KAN_IKKE_REAKTIVERES_FORENKLET,
                DOD_UTVANDRET_ELLER_FORSVUNNET, UKJENT_TEKNISK_FEIL,
                TEKNISK_FEIL -> false
            }
}