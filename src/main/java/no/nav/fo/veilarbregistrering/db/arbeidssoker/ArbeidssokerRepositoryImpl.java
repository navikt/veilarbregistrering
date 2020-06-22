package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private final JdbcTemplate db;

    public ArbeidssokerRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagre(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        SqlUtils.insert(db, "FORMIDLINGSGRUPPE")
                .value("ID", id)
                .value("FOEDSELSNUMMER", endretFormidlingsgruppeCommand.getFoedselsnummer()
                        .orElseThrow(() -> new IllegalStateException("Foedselsnummer var ikke satt. Skulle vært filtrert bort i forkant!"))
                        .stringValue())
                .value("FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getFormidlingsgruppe().stringValue())
                .value("FORMIDLINGSGRUPPE_ENDRET", endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret()
                        .map(Timestamp::valueOf)
                        .orElse(Timestamp.valueOf(LocalDateTime.MIN)))
                .execute();

        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }
}
