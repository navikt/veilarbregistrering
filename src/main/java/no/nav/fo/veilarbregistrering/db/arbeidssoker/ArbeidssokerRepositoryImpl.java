package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private final JdbcTemplate jdbcTemplate;

    public ArbeidssokerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long lagre(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        SqlUtils.insert(jdbcTemplate, "FORMIDLINGSGRUPPE")
                .value("ID", id)
                .value("FOEDSELSNUMMER", endretFormidlingsgruppeCommand.getFoedselsnummer()
                        .orElseThrow(() -> new IllegalStateException("Foedselsnummer var ikke satt. Skulle vært filtrert bort i forkant!"))
                        .stringValue())
                .value("FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getFormidlingsgruppe().stringValue())
                .value("FORMIDLINGSGRUPPE_ENDRET", Timestamp.valueOf(endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret()))
                .execute();

        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return jdbcTemplate.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }

    @Override
    public List<Arbeidssokerperiode> finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER = ?";
        return jdbcTemplate.query(
                sql,
                new Object[]{foedselsnummer.stringValue()},
                (rs, row) -> new Arbeidssokerperiode(
                        Formidlingsgruppe.of(rs.getString("FORMIDLINGSGRUPPE")),
                        Periode.of(
                                rs.getTimestamp("FORMIDLINGSGRUPPE_ENDRET").toLocalDateTime().toLocalDate(),
                                null)));
    }
}
