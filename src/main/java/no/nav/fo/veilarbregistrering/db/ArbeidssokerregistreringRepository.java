package no.nav.fo.veilarbregistrering.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.TekstForSporsmal;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import no.nav.fo.veilarbregistrering.utils.UtdanningUtils;
import no.nav.sbl.sql.DbConstants;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArbeidssokerregistreringRepository {

    private JdbcTemplate db;

    private final static String BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ";
    private final static String BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ";
    private final static String BRUKER_REGISTRERING = "BRUKER_REGISTRERING";
    private final static String BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING";
    private final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";
    private final static String BRUKER_REAKTIVERING_ID = "BRUKER_REAKTIVERING_ID";
    private final static String OPPRETTET_DATO = "OPPRETTET_DATO";
    private final static String REAKTIVERING_DATO = "REAKTIVERING_DATO";

    private final static String NUS_KODE = "NUS_KODE";
    private final static String YRKESPRAKSIS = "YRKESPRAKSIS";
    private final static String ENIG_I_OPPSUMMERING = "ENIG_I_OPPSUMMERING";
    private final static String OPPSUMMERING = "OPPSUMMERING";
    private final static String HAR_HELSEUTFORDRINGER = "HAR_HELSEUTFORDRINGER";
    private final static String YRKESBESKRIVELSE = "YRKESBESKRIVELSE";
    private final static String KONSEPT_ID = "KONSEPT_ID";
    private final static String TEKSTER_FOR_BESVARELSE = "TEKSTER_FOR_BESVARELSE";

    private final static String ANDRE_UTFORDRINGER = "ANDRE_UTFORDRINGER";
    private final static String BEGRUNNELSE_FOR_REGISTRERING = "BEGRUNNELSE_FOR_REGISTRERING";
    private final static String UTDANNING_BESTATT = "UTDANNING_BESTATT";
    private final static String UTDANNING_GODKJENT_NORGE = "UTDANNING_GODKJENT_NORGE";
    private final static String JOBBHISTORIKK = "JOBBHISTORIKK";

    private final static String AKTOR_ID = "AKTOR_ID";

    private final static String BRUKER_PROFILERING = "BRUKER_PROFILERING";
    private final static String PROFILERING_TYPE = "PROFILERING_TYPE";
    private final static String VERDI = "VERDI";

    private final static String ALDER = "ALDER";
    private final static String ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND";
    private final static String RESULTAT_PROFILERING = "RESULTAT_PROFILERING";

    public ArbeidssokerregistreringRepository(JdbcTemplate db) {
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

    public BrukerRegistrering lagreBruker(BrukerRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        Stilling stilling = bruker.getSisteStilling();
        String teksterForBesvarelse = tilJson(bruker.getTeksterForBesvarelse());

        SqlUtils.insert(db, BRUKER_REGISTRERING)
                .value(BRUKER_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
                .value(ENIG_I_OPPSUMMERING, bruker.isEnigIOppsummering())
                .value(OPPSUMMERING, bruker.getOppsummering())
                .value(TEKSTER_FOR_BESVARELSE, teksterForBesvarelse)
                // Siste stilling
                .value(YRKESPRAKSIS, stilling.getStyrk08())
                .value(YRKESBESKRIVELSE, stilling.getLabel())
                .value(KONSEPT_ID, stilling.getKonseptId())
                // Besvarelse
                .value(BEGRUNNELSE_FOR_REGISTRERING, besvarelse.getDinSituasjon().toString())
                .value(NUS_KODE, UtdanningUtils.mapTilNuskode(besvarelse.getUtdanning()))
                .value(UTDANNING_GODKJENT_NORGE, besvarelse.getUtdanningGodkjent().toString())
                .value(UTDANNING_BESTATT, besvarelse.getUtdanningBestatt().toString())
                .value(HAR_HELSEUTFORDRINGER, besvarelse.getHelseHinder().toString())
                .value(ANDRE_UTFORDRINGER, besvarelse.getAndreForhold().toString())
                .value(JOBBHISTORIKK, besvarelse.getSisteStilling().toString())
                .execute();

        return hentBrukerregistreringForId(id);
    }

    private static String tilJson(List<TekstForSporsmal> obj) {
        if (obj == null) {
            return "[]";
        }
        try {
            return (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private static List<TekstForSporsmal> tilTeksterForBesvarelse(String json) {
        try {
            TekstForSporsmal[] teksterForBesvarelse = (new ObjectMapper()).readValue(json, TekstForSporsmal[].class);
            return teksterForBesvarelse != null ? Arrays.asList(teksterForBesvarelse) : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public BrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, ArbeidssokerregistreringRepository::brukerRegistreringMapper)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .column("*")
                .execute();
    }

    public void lagreReaktiveringForBruker(AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REAKTIVERING_SEQ);
        SqlUtils.insert(db, BRUKER_REAKTIVERING)
                .value(BRUKER_REAKTIVERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(REAKTIVERING_DATO, DbConstants.CURRENT_TIMESTAMP)
                .execute();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }

    @SneakyThrows
    private static BrukerRegistrering brukerRegistreringMapper(ResultSet rs) {
        return new BrukerRegistrering()
                .setId(rs.getLong(BRUKER_REGISTRERING_ID))
                .setOpprettetDato(rs.getDate(OPPRETTET_DATO))
                .setEnigIOppsummering(rs.getBoolean(ENIG_I_OPPSUMMERING))
                .setOppsummering(rs.getString(OPPSUMMERING))
                .setTeksterForBesvarelse(tilTeksterForBesvarelse(rs.getString(TEKSTER_FOR_BESVARELSE)))
                .setSisteStilling(new Stilling()
                        .setStyrk08(rs.getString(YRKESPRAKSIS))
                        .setKonseptId(rs.getLong(KONSEPT_ID))
                        .setLabel(rs.getString(YRKESBESKRIVELSE)))
                .setBesvarelse(new Besvarelse()
                        .setDinSituasjon(DinSituasjonSvar.valueOf(rs.getString(BEGRUNNELSE_FOR_REGISTRERING)))
                        .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)))
                        .setUtdanningBestatt(UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT)))
                        .setUtdanningGodkjent(UtdanningGodkjentSvar.valueOf(rs.getString(UTDANNING_GODKJENT_NORGE)))
                        .setHelseHinder(HelseHinderSvar.valueOf(rs.getString(HAR_HELSEUTFORDRINGER)))
                        .setAndreForhold(AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER)))
                        .setSisteStilling(SisteStillingSvar.valueOf(rs.getString(JOBBHISTORIKK)))
                );
    }
}
