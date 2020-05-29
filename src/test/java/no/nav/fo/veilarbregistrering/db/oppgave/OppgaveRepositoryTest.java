package no.nav.fo.veilarbregistrering.db.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveImpl;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.List;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveRepositoryTest extends DbIntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private OppgaveRepository oppgaveRepository;

    @Before
    public void setup() {
        setupInMemoryDatabaseContext();
        oppgaveRepository = new OppgaveRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void opprettOppgave() {
        long id = oppgaveRepository.opprettOppgave(
                AktorId.valueOf("12353"),
                OppgaveType.OPPHOLDSTILLATELSE,
                3242L);

        assertThat(id).isNotEqualTo(0);
    }

    @Test
    public void hentOppgaveForAktorId() {
        long id = oppgaveRepository.opprettOppgave(
                AktorId.valueOf("12353"),
                OppgaveType.OPPHOLDSTILLATELSE,
                3242L);

        assertThat(id).isNotEqualTo(0);

        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.valueOf("12353"));
        OppgaveImpl oppgave = oppgaver.get(0);
        assertThat(oppgave.getId()).isEqualTo(id);
        assertThat(oppgave.getOppgavetype()).isEqualTo(OppgaveType.OPPHOLDSTILLATELSE);
        assertThat(oppgave.getEksternOppgaveId()).isEqualTo(3242L);
    }

    @Test
    public void hentOppgaveForUkjentAktorId() {
        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(AktorId.valueOf("12353"));
        assertThat(oppgaver).isEmpty();
    }
}