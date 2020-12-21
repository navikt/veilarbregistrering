package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class OrdinaerBrukerRegistreringMapper implements RowMapper<OrdinaerBrukerRegistrering> {

    static OrdinaerBrukerRegistrering map(ResultSet rs) {
        try {
            return new OrdinaerBrukerRegistrering()
                    .setId(rs.getLong(BrukerRegistreringRepositoryImpl.BRUKER_REGISTRERING_ID))
                    .setOpprettetDato(rs.getTimestamp(BrukerRegistreringRepositoryImpl.OPPRETTET_DATO).toLocalDateTime())
                    .setTeksterForBesvarelse(BrukerRegistreringRepositoryImpl.tilTeksterForBesvarelse(rs.getString(BrukerRegistreringRepositoryImpl.TEKSTER_FOR_BESVARELSE)))
                    .setSisteStilling(new Stilling()
                            .setStyrk08(rs.getString(BrukerRegistreringRepositoryImpl.YRKESPRAKSIS))
                            .setKonseptId(rs.getLong(BrukerRegistreringRepositoryImpl.KONSEPT_ID))
                            .setLabel(rs.getString(BrukerRegistreringRepositoryImpl.YRKESBESKRIVELSE)))
                    .setBesvarelse(new Besvarelse()
                            .setDinSituasjon(DinSituasjonSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.BEGRUNNELSE_FOR_REGISTRERING)))
                            .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(BrukerRegistreringRepositoryImpl.NUS_KODE)))
                            .setUtdanningBestatt(UtdanningBestattSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_BESTATT)))
                            .setUtdanningGodkjent(UtdanningGodkjentSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_GODKJENT_NORGE)))
                            .setHelseHinder(HelseHinderSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.HAR_HELSEUTFORDRINGER)))
                            .setAndreForhold(AndreForholdSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.ANDRE_UTFORDRINGER)))
                            .setSisteStilling(SisteStillingSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.JOBBHISTORIKK)))
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrdinaerBrukerRegistrering mapRow(ResultSet resultSet, int i) {
        return map(resultSet);
    }
}