package no.nav.fo.veilarbregistrering.db.registrering;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;

import java.sql.ResultSet;

class ManuellRegistreringMapper {

    @SneakyThrows
    static ManuellRegistrering map(ResultSet rs) {
        return new ManuellRegistrering()
                .setId(rs.getLong(ManuellRegistreringRepositoryImpl.MANUELL_REGISTRERING_ID))
                .setRegistreringId(rs.getLong(ManuellRegistreringRepositoryImpl.REGISTRERING_ID))
                .setBrukerRegistreringType(BrukerRegistreringType.valueOf(rs.getString(ManuellRegistreringRepositoryImpl.BRUKER_REGISTRERING_TYPE)))
                .setVeilederIdent(rs.getString(ManuellRegistreringRepositoryImpl.VEILEDER_IDENT))
                .setVeilederEnhetId(rs.getString(ManuellRegistreringRepositoryImpl.VEILEDER_ENHET_ID));
    }
}
