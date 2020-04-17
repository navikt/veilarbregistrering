package no.nav.fo.veilarbregistrering.profilering.db;

import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProfileringRepositoryImpl implements ProfileringRepository {

    private final JdbcTemplate db;

    private final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";

    private final static String BRUKER_PROFILERING = "BRUKER_PROFILERING";
    final static String PROFILERING_TYPE = "PROFILERING_TYPE";
    final static String VERDI = "VERDI";

    final static String ALDER = "ALDER";
    final static String ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND";
    final static String RESULTAT_PROFILERING = "RESULTAT_PROFILERING";

    public ProfileringRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
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

    @Override
    public Profilering hentProfileringForId(long brukerregistreringId){
        return SqlUtils.select(db, BRUKER_PROFILERING, ProfileringMapper::map)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .limit(3)
                .column("*")
                .execute();
    }
}
