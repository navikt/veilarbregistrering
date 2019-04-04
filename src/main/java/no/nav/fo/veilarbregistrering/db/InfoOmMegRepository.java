package no.nav.fo.veilarbregistrering.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.FremtidigSituasjonData;
import no.nav.fo.veilarbregistrering.domain.besvarelse.FremtidigSituasjonSvar;
import no.nav.sbl.sql.DbConstants;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.order.OrderClause;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

import static java.util.Optional.ofNullable;

public class InfoOmMegRepository {
    private JdbcTemplate db;
    private final static String INFO_OM_MEG = "INFO_OM_MEG";
    private final static String INFO_OM_MEG_SEQ = "INFO_OM_MEG_SEQ";

    private final static String AKTOR_ID = "AKTOR_ID";
    private final static String ALTERNATIV_ID = "ALTERNATIV_ID";
    private final static String ID = "INFO_OM_MEG_ID";

    private final static String SPM_ID = "SPM_ID";
    private final static String TEKST = "TEKST";
    private final static String ENDRET_AV = "ENDRET_AV";
    private final static String DATO = "DATO";

    private final static String FREMTIDIG_SITUASJON_ID = "FREMTIDIG_SITUASJON";

    public InfoOmMegRepository(JdbcTemplate db) {
        this.db = db;
    }

    public FremtidigSituasjonData hentFremtidigSituasjonForAktorId(AktorId aktorId) {
        return SqlUtils.select(db, INFO_OM_MEG, InfoOmMegRepository::fremtidigSituasjonMapper)
                .where(WhereClause.equals(AKTOR_ID, aktorId.getAktorId())
                        .and(WhereClause.equals(SPM_ID, FREMTIDIG_SITUASJON_ID)))
                .orderBy(OrderClause.desc(DATO))
                .limit(1)
                .column("*")
                .execute();
    }

    public void lagreFremtidigSituasjonForAktorId(FremtidigSituasjonData fremtidigSituasjonData, AktorId aktorId, String endretAv) {
        long id = nesteFraSekvens(INFO_OM_MEG_SEQ);
        String alt = fremtidigSituasjonData.getAlternativId().toString();
        SqlUtils.insert(db, INFO_OM_MEG)
                .value(ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(SPM_ID, FREMTIDIG_SITUASJON_ID)
                .value(ALTERNATIV_ID, alt)
                .value(TEKST, fremtidigSituasjonData.getTekst())
                .value(ENDRET_AV, endretAv)
                .value(DATO, DbConstants.CURRENT_TIMESTAMP)
                .execute();
    }

    @SneakyThrows
    private static FremtidigSituasjonData fremtidigSituasjonMapper(ResultSet rs) {
        return new FremtidigSituasjonData()
                .setAlternativId(ofNullable(rs.getString(ALTERNATIV_ID)).isPresent()
                        ? FremtidigSituasjonSvar.valueOf(rs.getString(ALTERNATIV_ID))
                        : null
                )
                .setTekst(rs.getString(TEKST));

    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }
}
