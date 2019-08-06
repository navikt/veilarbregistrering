package no.nav.fo.veilarbregistrering.registrering.bruker.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.*;

import java.sql.ResultSet;

import static java.util.Optional.ofNullable;

class SykmeldtRegistreringMapper {

    @SneakyThrows
    static SykmeldtRegistrering map(ResultSet rs) {
        return new SykmeldtRegistrering()
                .setId(rs.getLong(BrukerRegistreringRepositoryImpl.SYKMELDT_REGISTRERING_ID))
                .setOpprettetDato(rs.getTimestamp(BrukerRegistreringRepositoryImpl.OPPRETTET_DATO).toLocalDateTime())
                .setTeksterForBesvarelse(BrukerRegistreringRepositoryImpl.tilTeksterForBesvarelse(rs.getString(BrukerRegistreringRepositoryImpl.TEKSTER_FOR_BESVARELSE)))
                .setBesvarelse(new Besvarelse()
                        .setFremtidigSituasjon(
                                ofNullable(rs.getString(BrukerRegistreringRepositoryImpl.FREMTIDIG_SITUASJON)).isPresent()
                                        ? FremtidigSituasjonSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.FREMTIDIG_SITUASJON))
                                        : null
                        )
                        .setTilbakeIArbeid(
                                ofNullable(rs.getString(BrukerRegistreringRepositoryImpl.TILBAKE_ETTER_52_UKER)).isPresent()
                                        ? TilbakeIArbeidSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.TILBAKE_ETTER_52_UKER))
                                        : null
                        )
                        .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(BrukerRegistreringRepositoryImpl.NUS_KODE)))
                        .setUtdanningBestatt(
                                ofNullable(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_BESTATT)).isPresent()
                                        ? UtdanningBestattSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_BESTATT))
                                        : null
                        )
                        .setUtdanningGodkjent(
                                ofNullable(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_GODKJENT_NORGE)).isPresent()
                                        ? UtdanningGodkjentSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_GODKJENT_NORGE))
                                        : null
                        )
                        .setAndreForhold(
                                ofNullable(rs.getString(BrukerRegistreringRepositoryImpl.ANDRE_UTFORDRINGER)).isPresent()
                                        ? AndreForholdSvar.valueOf(rs.getString(BrukerRegistreringRepositoryImpl.ANDRE_UTFORDRINGER))
                                        : null
                        )
                );
    }
}
