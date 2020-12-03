package no.nav.fo.veilarbregistrering.db.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveImpl;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig
@Transactional(transactionManager = "txMgrTest")
@ContextConfiguration(classes = {DatabaseConfig.class, RepositoryConfig.class})
public class OppgaveRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OppgaveRepository oppgaveRepository;

    @BeforeAll
    public void setup() {
        MigrationUtils.createTables(jdbcTemplate);
    }

    @Test
    public void opprettOppgave() {
        long id = oppgaveRepository.opprettOppgave(
                AktorId.of("12353"),
                OppgaveType.OPPHOLDSTILLATELSE,
                3242L);

        assertThat(id).isNotEqualTo(0);
    }

    @Test
    public void hentOppgaveForAktorId() {
        long id = oppgaveRepository.opprettOppgave(
                AktorId.of("12353"),
                OppgaveType.OPPHOLDSTILLATELSE,
                3242L);

        assertThat(id).isNotEqualTo(0);

        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.of("12353"));
        OppgaveImpl oppgave = oppgaver.get(0);
        assertThat(oppgave.getId()).isEqualTo(id);
        assertThat(oppgave.getOppgavetype()).isEqualTo(OppgaveType.OPPHOLDSTILLATELSE);
        assertThat(oppgave.getEksternOppgaveId()).isEqualTo(3242L);
    }

    @Test
    public void hentOppgaveForUkjentAktorId() {
        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.of("12353"));
        assertThat(oppgaver).isEmpty();
    }
}