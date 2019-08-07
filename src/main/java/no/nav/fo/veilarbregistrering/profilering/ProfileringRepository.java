package no.nav.fo.veilarbregistrering.profilering;

import lombok.SneakyThrows;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

public class ProfileringRepository {

    private JdbcTemplate db;

    private final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";

    private final static String BRUKER_PROFILERING = "BRUKER_PROFILERING";
    private final static String PROFILERING_TYPE = "PROFILERING_TYPE";
    private final static String VERDI = "VERDI";

    private final static String ALDER = "ALDER";
    private final static String ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND";
    private final static String RESULTAT_PROFILERING = "RESULTAT_PROFILERING";

    public ProfileringRepository(JdbcTemplate db) {
        this.db = db;
    }

    public void lagreProfilering(long brukerregistreringId, Profilering profilering) {
        SqlUtils.insert(db, BRUKER_PROFILERING)
                .value(BRUKER_REGISTRERING_ID, brukerregistreringId)
                .value(PROFILERING_TYPE, ALDER)
                .value(VERDI, profilering.getAlder())
                .execute();

        SqlUtils.insert(db, BRUKER_PROFILERING)
                .value(BRUKER_REGISTRERING_ID, brukerregistreringId)
                .value(PROFILERING_TYPE, ARB_6_AV_SISTE_12_MND)
                .value(VERDI, profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder())
                .execute();

        SqlUtils.insert(db, BRUKER_PROFILERING)
                .value(BRUKER_REGISTRERING_ID, brukerregistreringId)
                .value(PROFILERING_TYPE, RESULTAT_PROFILERING)
                .value(VERDI, profilering.getInnsatsgruppe().getArenakode())
                .execute();
    }

    public Profilering hentProfileringForId(long brukerregistreringId){
        return SqlUtils.select(db, BRUKER_PROFILERING, ProfileringRepository::brukerProfileringMapper)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .limit(3)
                .column("*")
                .execute();
    }

    @SneakyThrows
    private static Profilering brukerProfileringMapper(ResultSet rs) {

        Profilering profilering = new Profilering();

        do {
            switch (rs.getString(PROFILERING_TYPE)){
                case ALDER:
                    profilering.setAlder(rs.getInt(VERDI));
                    break;
                case ARB_6_AV_SISTE_12_MND:
                    profilering.setJobbetSammenhengendeSeksAvTolvSisteManeder(rs.getBoolean(VERDI));
                    break;
                case RESULTAT_PROFILERING:
                    profilering.setInnsatsgruppe(Innsatsgruppe.tilInnsatsgruppe(rs.getString(VERDI)));
                    break;
            }

        } while (rs.next());

        return profilering;
    }

}
