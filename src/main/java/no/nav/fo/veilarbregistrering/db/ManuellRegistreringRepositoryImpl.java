package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

public class ManuellRegistreringRepositoryImpl implements ManuellRegistreringRepository {

    private JdbcTemplate db;

    private final static String MANUELL_REGISTRERING_SEQ = "MANUELL_REGISTRERING_SEQ";
    private final static String MANUELL_REGISTRERING = "MANUELL_REGISTRERING";
    final static String MANUELL_REGISTRERING_ID = "MANUELL_REGISTRERING_ID";

    final static String REGISTRERING_ID = "REGISTRERING_ID";
    final static String BRUKER_REGISTRERING_TYPE = "BRUKER_REGISTRERING_TYPE";
    final static String VEILEDER_IDENT = "VEILEDER_IDENT";
    final static String VEILEDER_ENHET_ID = "VEILEDER_ENHET_ID";

    public ManuellRegistreringRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagreManuellRegistrering(ManuellRegistrering manuellRegistrering) {
        long id = nesteFraSekvens(MANUELL_REGISTRERING_SEQ);
        SqlUtils.insert(db, MANUELL_REGISTRERING)
                .value(MANUELL_REGISTRERING_ID, id)
                .value(REGISTRERING_ID, manuellRegistrering.getRegistreringId())
                .value(BRUKER_REGISTRERING_TYPE, manuellRegistrering.getBrukerRegistreringType().toString())
                .value(VEILEDER_IDENT, manuellRegistrering.getVeilederIdent())
                .value(VEILEDER_ENHET_ID, manuellRegistrering.getVeilederEnhetId())
                .execute();
        return id;
    }

    @Override
    public ManuellRegistrering hentManuellRegistrering(long registreringId, BrukerRegistreringType brukerRegistreringType) {
        return SqlUtils.select(db, MANUELL_REGISTRERING, ManuellRegistreringMapper::map)
                .where(WhereClause.equals(REGISTRERING_ID, registreringId)
                        .and(WhereClause.equals(BRUKER_REGISTRERING_TYPE, brukerRegistreringType.toString())))
                .limit(1)
                .column("*")
                .execute();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }


}
