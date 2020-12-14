package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;

import java.sql.ResultSet;
import java.sql.SQLException;

class ManuellRegistreringMapper {

    static ManuellRegistrering map(ResultSet rs) {
        try {
            return new ManuellRegistrering()
                    .setId(rs.getLong(ManuellRegistreringRepositoryImpl.MANUELL_REGISTRERING_ID))
                    .setRegistreringId(rs.getLong(ManuellRegistreringRepositoryImpl.REGISTRERING_ID))
                    .setBrukerRegistreringType(BrukerRegistreringType.valueOf(rs.getString(ManuellRegistreringRepositoryImpl.BRUKER_REGISTRERING_TYPE)))
                    .setVeilederIdent(rs.getString(ManuellRegistreringRepositoryImpl.VEILEDER_IDENT))
                    .setVeilederEnhetId(rs.getString(ManuellRegistreringRepositoryImpl.VEILEDER_ENHET_ID));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}