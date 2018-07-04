package no.nav.fo.veilarbregistrering.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
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

    private final static String ANDRE_UTFORDRINGER = "ANDRE_UTFORDRINGER";
    private final static String BEGRUNNELSE_FOR_REGISTRERING = "BEGRUNNELSE_FOR_REGISTRERING";
    private final static String UTDANNING_BESTATT = "UTDANNING_BESTATT";
    private final static String UTDANNING_GODKJENT_NORGE = "UTDANNING_GODKJENT_NORGE";

    private final static String AKTOR_ID = "AKTOR_ID";

    public ArbeidssokerregistreringRepository(JdbcTemplate db) {
        this.db = db;
    }

    public BrukerRegistrering lagreBruker(BrukerRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        Stilling stilling = bruker.getSisteStilling();
        int helseHinder = HelseHinderSvar.JA.equals(besvarelse.getHelseHinder()) ? 1 : 0;

        SqlUtils.insert(db, BRUKER_REGISTRERING)
                .value(BRUKER_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
                .value(ENIG_I_OPPSUMMERING, bruker.isEnigIOppsummering())
                .value(OPPSUMMERING, bruker.getOppsummering())
                // Siste stilling
                .value(YRKESPRAKSIS, stilling.getStyrk08())
                .value(YRKESBESKRIVELSE, stilling.getLabel())
                .value(KONSEPT_ID, stilling.getKonseptId())
                // Besvarelse
                .value(BEGRUNNELSE_FOR_REGISTRERING, besvarelse.getDinSituasjon().toString())
                .value(NUS_KODE, bruker.getNusKode())
                .value(UTDANNING_GODKJENT_NORGE, besvarelse.getUtdanningGodkjent().toString())
                .value(UTDANNING_BESTATT, besvarelse.getUtdanningBestatt().toString())
                .value(HAR_HELSEUTFORDRINGER, helseHinder)
                .value(ANDRE_UTFORDRINGER, besvarelse.getAndreForhold().toString())
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
        HelseHinderSvar helseHinder = rs.getInt(HAR_HELSEUTFORDRINGER) == 0
                ? HelseHinderSvar.NEI
                : HelseHinderSvar.JA;
        return new BrukerRegistrering()
                .setId(rs.getLong(BRUKER_REGISTRERING_ID))
                .setNusKode(rs.getString(NUS_KODE))

                .setOpprettetDato(rs.getDate(OPPRETTET_DATO))
                .setEnigIOppsummering(rs.getBoolean(ENIG_I_OPPSUMMERING))
                .setOppsummering(rs.getString(OPPSUMMERING))
                .setSisteStilling(new Stilling()
                        .setStyrk08(rs.getString(YRKESPRAKSIS))
                        .setKonseptId(rs.getLong(KONSEPT_ID))
                        .setLabel(rs.getString(YRKESBESKRIVELSE)))
                .setBesvarelse(new Besvarelse()
                        .setDinSituasjon(DinSituasjonSvar.valueOf(rs.getString(BEGRUNNELSE_FOR_REGISTRERING)))
                        .setUtdanningBestatt(UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT)))
                        .setUtdanningGodkjent(UtdanningGodkjentSvar.valueOf(rs.getString(UTDANNING_GODKJENT_NORGE)))
                        .setHelseHinder(helseHinder)
                        .setAndreForhold(AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER)))
                );
    }

}
