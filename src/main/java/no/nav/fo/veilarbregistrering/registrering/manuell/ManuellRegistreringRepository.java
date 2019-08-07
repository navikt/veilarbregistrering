package no.nav.fo.veilarbregistrering.registrering.manuell;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

public class ManuellRegistreringRepository {

    private JdbcTemplate db;

    private final static String MANUELL_REGISTRERING_SEQ = "MANUELL_REGISTRERING_SEQ";
    private final static String MANUELL_REGISTRERING = "MANUELL_REGISTRERING";
    private final static String MANUELL_REGISTRERING_ID = "MANUELL_REGISTRERING_ID";

    private final static String REGISTRERING_ID = "REGISTRERING_ID";
    private final static String BRUKER_REGISTRERING_TYPE = "BRUKER_REGISTRERING_TYPE";
    private final static String VEILEDER_IDENT = "VEILEDER_IDENT";
    private final static String VEILEDER_ENHET_ID = "VEILEDER_ENHET_ID";

    public ManuellRegistreringRepository(JdbcTemplate db) {
        this.db = db;
    }

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

    public ManuellRegistrering hentManuellRegistrering(long registreringId, BrukerRegistreringType brukerRegistreringType) {
        return SqlUtils.select(db, MANUELL_REGISTRERING, ManuellRegistreringRepository::manuellRegistreringMapper)
                .where(WhereClause.equals(REGISTRERING_ID, registreringId)
                        .and(WhereClause.equals(BRUKER_REGISTRERING_TYPE, brukerRegistreringType.toString())))
                .limit(1)
                .column("*")
                .execute();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }

    @SneakyThrows
    private static ManuellRegistrering manuellRegistreringMapper(ResultSet rs) {
        return new ManuellRegistrering()
                .setId(rs.getLong(MANUELL_REGISTRERING_ID))
                .setRegistreringId(rs.getLong(REGISTRERING_ID))
                .setBrukerRegistreringType(BrukerRegistreringType.valueOf(rs.getString(BRUKER_REGISTRERING_TYPE)))
                .setVeilederIdent(rs.getString(VEILEDER_IDENT))
                .setVeilederEnhetId(rs.getString(VEILEDER_ENHET_ID));
    }

}
