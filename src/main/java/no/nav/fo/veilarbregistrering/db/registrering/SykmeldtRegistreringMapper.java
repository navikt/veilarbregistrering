package no.nav.fo.veilarbregistrering.db.registrering;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.besvarelse.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl.*;

class SykmeldtRegistreringMapper {

    @SneakyThrows
    static SykmeldtRegistrering map(ResultSet rs) {
        return new SykmeldtRegistrering()
                .setId(rs.getLong(SYKMELDT_REGISTRERING_ID))
                .setOpprettetDato(rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime())
                .setTeksterForBesvarelse(BrukerRegistreringRepositoryImpl.tilTeksterForBesvarelse(rs.getString(TEKSTER_FOR_BESVARELSE)))
                .setBesvarelse(besvarelse(rs));
    }

    private static Besvarelse besvarelse(ResultSet rs) throws SQLException {
        return new Besvarelse()
                .setFremtidigSituasjon(ofNullable(rs.getString(FREMTIDIG_SITUASJON)).map(FremtidigSituasjonSvar::valueOf).orElse(null))
                .setTilbakeIArbeid(ofNullable(rs.getString(TILBAKE_ETTER_52_UKER)).map(TilbakeIArbeidSvar::valueOf).orElse(null))
                .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(BrukerRegistreringRepositoryImpl.NUS_KODE)))
                .setUtdanningBestatt(ofNullable(rs.getString(UTDANNING_BESTATT)).map(UtdanningBestattSvar::valueOf).orElse(null))
                .setUtdanningGodkjent(ofNullable(rs.getString(UTDANNING_GODKJENT_NORGE)).map(UtdanningGodkjentSvar::valueOf).orElse(null))
                .setAndreForhold(ofNullable(rs.getString(ANDRE_UTFORDRINGER)).map(AndreForholdSvar::valueOf).orElse(null));
    }

}
