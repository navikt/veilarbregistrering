package no.nav.fo.veilarbregistrering.db.oppgave

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType.OPPHOLDSTILLATELSE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class OppgaveRepositoryTest(

    @Autowired
    private val oppgaveRepository: OppgaveRepository) {

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