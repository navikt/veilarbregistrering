package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.*
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.LocalDateTime

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class FormidlingsgruppeRepositoryDbIntegrationTest(

    @Autowired
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository
) {

    @Test
    fun `skal kun lagre melding en gang`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20))
        var id = formidlingsgruppeRepository.lagre(command)
        Assertions.assertThat(id).isNotNull
        id = formidlingsgruppeRepository.lagre(command)
        Assertions.assertThat(id).isEqualTo(-1)
    }

    @Test
    fun `skal lagre formidlingsgruppeEvent`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20))
        val id = formidlingsgruppeRepository.lagre(command)
        Assertions.assertThat(id).isNotNull
        val arbeidssokerperiodes = formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(listOf(FOEDSELSNUMMER))
        val arbeidssokerperiode = Arbeidssokerperiode.of(Periode(LocalDate.now(), null))
        Assertions.assertThat(arbeidssokerperiodes.asList()).containsOnly(arbeidssokerperiode)
    }

    @Test
    fun `skal hente alle periodene for en persons identer`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusDays(10))
        formidlingsgruppeRepository.lagre(command)
        val command2 = endretFormdlingsgruppe(FOEDSELSNUMMER_2, LocalDateTime.now().minusDays(50))
        formidlingsgruppeRepository.lagre(command2)
        val command3 = endretFormdlingsgruppe(FOEDSELSNUMMER_3, LocalDateTime.now().minusSeconds(20))
        formidlingsgruppeRepository.lagre(command3)
        val bruker = Bruker.of(FOEDSELSNUMMER, AKTORID, listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_3))
        val arbeidssokerperiodes = formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(bruker.alleFoedselsnummer())
        Assertions.assertThat(arbeidssokerperiodes.asList()).hasSize(3)
    }

    private fun endretFormdlingsgruppe(foedselsnummer: Foedselsnummer, tidspunkt: LocalDateTime): FormidlingsgruppeEvent {
        return FormidlingsgruppeEvent(
            foedselsnummer,
            "123456",
            "AKTIV",
            Operation.UPDATE,
            Formidlingsgruppe("ARBS"),
            tidspunkt,
            null,
            null
        )
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer("01234567890")
        private val AKTORID = AktorId("1000010000100")
        private val FOEDSELSNUMMER_2 = Foedselsnummer("01234567892")
        private val FOEDSELSNUMMER_3 = Foedselsnummer("01234567895")
    }
}