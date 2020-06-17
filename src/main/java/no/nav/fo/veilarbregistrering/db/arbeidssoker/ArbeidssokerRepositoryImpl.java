package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private final JdbcTemplate db;

    public ArbeidssokerRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagre(ArenaFormidlingsgruppeEvent arenaFormidlingsgruppeEvent) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        SqlUtils.insert(db, "FORMIDLINGSGRUPPE")
                .value("ID", id)
                .value("FOEDSELSNUMMER", arenaFormidlingsgruppeEvent.getFoedselsnummer().stringValue())
                .value("FORMIDLINGSGRUPPE", arenaFormidlingsgruppeEvent.getFormidlingsgruppe().stringValue())
                .value("FORMIDLINGSGRUPPE_ENDRET", arenaFormidlingsgruppeEvent.getFormidlingsgruppeEndret())
                .execute();

        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }
}
