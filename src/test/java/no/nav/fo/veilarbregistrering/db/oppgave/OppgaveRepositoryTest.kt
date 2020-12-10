package no.nav.fo.veilarbregistrering.db.oppgave

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.MigrationUtils
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.db.TransactionalTest
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType.OPPHOLDSTILLATELSE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration

@TransactionalTest
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class])
open class OppgaveRepositoryTest(

    @Autowired
    private val jdbcTemplate: JdbcTemplate,
    @Autowired
    private val oppgaveRepository: OppgaveRepository) {

    @BeforeEach
    fun setup() {
        MigrationUtils.createTables(jdbcTemplate)
    }

    @Test
    fun opprettOppgave() {
        val id = oppgaveRepository.opprettOppgave(
                AktorId.of("12353"),
                OPPHOLDSTILLATELSE,
                3242L)
        assertThat(id).isNotEqualTo(0)
    }

    @Test
    fun hentOppgaveForAktorId() {
        val id = oppgaveRepository.opprettOppgave(
                AktorId.of("12353"),
                OPPHOLDSTILLATELSE,
                3242L)
        assertThat(id).isNotEqualTo(0)
        val oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.of("12353"))
        val oppgave = oppgaver[0]
        assertThat(oppgave.id).isEqualTo(id)
        assertThat(oppgave.oppgavetype).isEqualTo(OPPHOLDSTILLATELSE)
        assertThat(oppgave.eksternOppgaveId).isEqualTo(3242L)
    }

    @Test
    fun hentOppgaveForUkjentAktorId() {
        val oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.of("12353"))
        assertThat(oppgaver).isEmpty()
    }
}