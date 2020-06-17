package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private final JdbcTemplate db;

    public ArbeidssokerRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagre(FormidlingsgruppeEvent formidlingsgruppeEvent) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        // TODO Legge til "endret" når det er klart
        SqlUtils.insert(db, "FORMIDLINGSGRUPPE")
                .value("ID", id)
                .value("FOEDSELSNUMMER", formidlingsgruppeEvent.getFoedselsnummer().stringValue())
                .value("FORMIDLINGSGRUPPE", formidlingsgruppeEvent.getFormidlingsgruppe().stringValue())
                .execute();

        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }
}
