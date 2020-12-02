package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static java.util.Optional.ofNullable;

class RegistreringTilstandMapper implements RowMapper<RegistreringTilstand> {

    @Override
    public RegistreringTilstand mapRow(ResultSet rs, int i) throws SQLException {
        return RegistreringTilstand.of(
                rs.getLong("ID"),
                rs.getLong("BRUKER_REGISTRERING_ID"),
                rs.getTimestamp("OPPRETTET").toLocalDateTime(),
                ofNullable(rs.getTimestamp("SIST_ENDRET"))
                        .map(Timestamp::toLocalDateTime)
                        .orElse(null),
                Status.valueOf(rs.getString("STATUS")));
    }
}
