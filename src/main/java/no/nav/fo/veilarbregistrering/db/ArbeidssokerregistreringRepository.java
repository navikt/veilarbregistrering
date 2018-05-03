package no.nav.fo.veilarbregistrering.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.sbl.sql.DbConstants;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

public class ArbeidssokerregistreringRepository {

    private JdbcTemplate db;

    private final static String BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ";
    private final static String BRUKER_REGISTRERING = "BRUKER_REGISTRERING";
    private final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";
    private final static String OPPRETTET_DATO = "OPPRETTET_DATO";

    private final static String NUS_KODE = "NUS_KODE";
    private final static String YRKESPRAKSIS = "YRKESPRAKSIS";
    private final static String ENIG_I_OPPSUMMERING = "ENIG_I_OPPSUMMERING";
    private final static String OPPSUMMERING = "OPPSUMMERING";
    private final static String HAR_HELSEUTFORDRINGER = "HAR_HELSEUTFORDRINGER";
    private final static String YRKESBESKRIVELSE = "YRKESBESKRIVELSE";
    private final static String KONSEPT_ID = "KONSEPT_ID";

    private final static String AKTOR_ID = "AKTOR_ID";

    public ArbeidssokerregistreringRepository(JdbcTemplate db) {
        this.db = db;
    }

    public BrukerRegistrering lagreBruker(BrukerRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ);
        SqlUtils.insert(db, BRUKER_REGISTRERING)
                .value(BRUKER_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
                .value(NUS_KODE, bruker.getNusKode())
                .value(YRKESPRAKSIS, bruker.getYrkesPraksis())
                .value(ENIG_I_OPPSUMMERING, bruker.isEnigIOppsummering())
                .value(OPPSUMMERING, bruker.getOppsummering())
                .value(HAR_HELSEUTFORDRINGER, bruker.isHarHelseutfordringer())
                .value(YRKESBESKRIVELSE, -1)
                .value(KONSEPT_ID, -1)
                .execute();

        return hentBrukerregistreringForId(id);
    }

    public BrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, ArbeidssokerregistreringRepository::brukerRegistreringMapper)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .column("*")
                .execute();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }

    @SneakyThrows
    private static BrukerRegistrering brukerRegistreringMapper(ResultSet rs) {
        return BrukerRegistrering.builder()
                .id(rs.getLong(BRUKER_REGISTRERING_ID))
                .nusKode(rs.getString(NUS_KODE))
                .yrkesPraksis(rs.getString(YRKESPRAKSIS))
                .opprettetDato(rs.getDate(OPPRETTET_DATO))
                .enigIOppsummering(rs.getBoolean(ENIG_I_OPPSUMMERING))
                .oppsummering(rs.getString(OPPSUMMERING))
                .harHelseutfordringer(rs.getBoolean(HAR_HELSEUTFORDRINGER))
                .build();
    }
}
