package no.nav.fo.veilarbregistrering.oppgave.db;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveImpl;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OppgaveRepositoryImpl implements OppgaveRepository {

    private final JdbcTemplate jdbcTemplate;

    public OppgaveRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long opprettOppgave(
            AktorId aktorId,
            OppgaveType oppgaveType,
            long eksternOppgaveId) {

        long id = nesteFraSekvens();
        SqlUtils.insert(jdbcTemplate, "OPPGAVE")
                .value("ID", id)
                .value("AKTOR_ID", aktorId.asString())
                .value("OPPGAVETYPE", oppgaveType.name())
                .value("EKSTERN_OPPGAVE_ID", eksternOppgaveId)
                .value("OPPRETTET", Timestamp.valueOf(LocalDateTime.now()))
                .execute();

        return id;
    }

    private long nesteFraSekvens() {
        return jdbcTemplate.queryForObject("select OPPGAVE_SEQ.nextval from dual", Long.class).longValue();
    }

    @Override
    public OppgaveImpl hentOppgaveFor(AktorId aktorId) {
        String sql = "SELECT * FROM OPPGAVE WHERE AKTOR_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{aktorId.asString()}, (rs, row) -> {
            return new OppgaveImpl(
                    rs.getLong("ID"),
                    AktorId.valueOf(rs.getString("AKTOR_ID")),
                    OppgaveType.valueOf(rs.getString("OPPGAVETYPE")),
                    rs.getLong("EKSTERN_OPPGAVE_ID"),
                    rs.getTimestamp("OPPRETTET").toLocalDateTime());
        });
    }
}
