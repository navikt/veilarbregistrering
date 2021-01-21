package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.*
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.MigrationUtils
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class ArbeidssokerRepositoryDbIntegrationTest(

    @Autowired
    private val arbeidssokerRepository: ArbeidssokerRepository,
    @Autowired
    private val jdbcTemplate: JdbcTemplate) {

    @BeforeEach
    fun setup() {
        MigrationUtils.createTables(jdbcTemplate)
    }

    @Test
    fun `skal kun lagre melding en gang`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20))
        var id = arbeidssokerRepository.lagre(command)
        Assertions.assertThat(id).isNotNull()
        id = arbeidssokerRepository.lagre(command)
        Assertions.assertThat(id).isEqualTo(-1)
    }

    @Test
    fun `skal lagre formidlingsgruppeEvent`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20))
        val id = arbeidssokerRepository.lagre(command)
        Assertions.assertThat(id).isNotNull()
        val arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(FOEDSELSNUMMER)
        val arbeidssokerperiode = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.now(), null))
        Assertions.assertThat(arbeidssokerperiodes.asList()).containsOnly(arbeidssokerperiode)
    }

    @Test
    fun `skal hente alle periodene for en persons identer`() {
        val command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusDays(10))
        arbeidssokerRepository.lagre(command)
        val command2 = endretFormdlingsgruppe(FOEDSELSNUMMER_2, LocalDateTime.now().minusDays(50))
        arbeidssokerRepository.lagre(command2)
        val command3 = endretFormdlingsgruppe(FOEDSELSNUMMER_3, LocalDateTime.now().minusSeconds(20))
        arbeidssokerRepository.lagre(command3)
        val bruker = Bruker.of(FOEDSELSNUMMER, null, Arrays.asList(FOEDSELSNUMMER_2, FOEDSELSNUMMER_3))
        val arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(bruker.alleFoedselsnummer())
        Assertions.assertThat(arbeidssokerperiodes.asList()).hasSize(3)
    }

    private fun endretFormdlingsgruppe(foedselsnummer: Foedselsnummer, tidspunkt: LocalDateTime): EndretFormidlingsgruppeCommand {
        return object : EndretFormidlingsgruppeCommand {
            override fun getFoedselsnummer(): Optional<Foedselsnummer> {
                return Optional.of(foedselsnummer)
            }

            override fun getPersonId(): String {
                return "123456"
            }

            override fun getPersonIdStatus(): String {
                return "AKTIV"
            }

            override fun getOperation(): Operation {
                return Operation.UPDATE
            }

            override fun getFormidlingsgruppe(): Formidlingsgruppe {
                return Formidlingsgruppe.of("ARBS")
            }

            override fun getFormidlingsgruppeEndret(): LocalDateTime {
                return tidspunkt
            }

            override fun getForrigeFormidlingsgruppe(): Optional<Formidlingsgruppe> {
                return Optional.empty()
            }

            override fun getForrigeFormidlingsgruppeEndret(): Optional<LocalDateTime> {
                return Optional.empty()
            }
        }
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer.of("01234567890")
        private val FOEDSELSNUMMER_2 = Foedselsnummer.of("01234567892")
        private val FOEDSELSNUMMER_3 = Foedselsnummer.of("01234567895")
    }
}