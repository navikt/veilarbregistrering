package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiveringTilstand;
import no.nav.fo.veilarbregistrering.registrering.bruker.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static java.util.Optional.ofNullable;

class AktiveringTilstandMapper implements RowMapper<AktiveringTilstand> {

    @Override
    public AktiveringTilstand mapRow(ResultSet rs, int i) throws SQLException {
        return AktiveringTilstand.of(
                rs.getLong("ID"),
                UUID.fromString(rs.getString("UUID")),
                rs.getLong("BRUKER_REGISTRERING_ID"),
                rs.getTimestamp("OPPRETTET").toLocalDateTime(),
                ofNullable(rs.getTimestamp("SIST_ENDRET"))
                        .map(Timestamp::toLocalDateTime)
                        .orElse(null),
                Status.valueOf(rs.getString("STATUS")));
    }
}
