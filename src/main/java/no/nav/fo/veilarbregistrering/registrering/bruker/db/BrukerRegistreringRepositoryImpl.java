package no.nav.fo.veilarbregistrering.registrering.bruker.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.sbl.sql.DbConstants;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.order.OrderClause;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class BrukerRegistreringRepositoryImpl implements BrukerRegistreringRepository {

    private JdbcTemplate db;

    private final static String SYKMELDT_REGISTRERING_SEQ = "SYKMELDT_REGISTRERING_SEQ";
    final static String SYKMELDT_REGISTRERING_ID = "SYKMELDT_REGISTRERING_ID";
    private final static String SYKMELDT_REGISTRERING = "SYKMELDT_REGISTRERING";
    final static String FREMTIDIG_SITUASJON = "FREMTIDIG_SITUASJON";
    final static String TILBAKE_ETTER_52_UKER = "TILBAKE_ETTER_52_UKER";

    private final static String REGISTRERING_TILSTAND_SEQ = "REGISTRERING_TILSTAND_SEQ";

    private final static String BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ";
    private final static String BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ";
    private final static String BRUKER_REGISTRERING = "BRUKER_REGISTRERING";
    private final static String BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING";
    final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";
    private final static String BRUKER_REAKTIVERING_ID = "BRUKER_REAKTIVERING_ID";
    final static String OPPRETTET_DATO = "OPPRETTET_DATO";
    private final static String REAKTIVERING_DATO = "REAKTIVERING_DATO";

    final static String NUS_KODE = "NUS_KODE";
    final static String YRKESPRAKSIS = "YRKESPRAKSIS";
    final static String HAR_HELSEUTFORDRINGER = "HAR_HELSEUTFORDRINGER";
    final static String YRKESBESKRIVELSE = "YRKESBESKRIVELSE";
    final static String KONSEPT_ID = "KONSEPT_ID";
    final static String TEKSTER_FOR_BESVARELSE = "TEKSTER_FOR_BESVARELSE";

    final static String ANDRE_UTFORDRINGER = "ANDRE_UTFORDRINGER";
    final static String BEGRUNNELSE_FOR_REGISTRERING = "BEGRUNNELSE_FOR_REGISTRERING";
    final static String UTDANNING_BESTATT = "UTDANNING_BESTATT";
    final static String UTDANNING_GODKJENT_NORGE = "UTDANNING_GODKJENT_NORGE";
    final static String JOBBHISTORIKK = "JOBBHISTORIKK";

    private final static String AKTOR_ID = "AKTOR_ID";

    public BrukerRegistreringRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public OrdinaerBrukerRegistrering lagreOrdinaerBruker(OrdinaerBrukerRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        Stilling stilling = bruker.getSisteStilling();
        String teksterForBesvarelse = tilJson(bruker.getTeksterForBesvarelse());

        SqlUtils.insert(db, BRUKER_REGISTRERING)
                .value(BRUKER_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.asString())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
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

    @Override
    public long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(SYKMELDT_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        String teksterForBesvarelse = tilJson(bruker.getTeksterForBesvarelse());

        SqlUtils.insert(db, SYKMELDT_REGISTRERING)
                .value(SYKMELDT_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.asString())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
                .value(TEKSTER_FOR_BESVARELSE, teksterForBesvarelse)
                // Besvarelse
                .value(FREMTIDIG_SITUASJON, ofNullable(besvarelse.getFremtidigSituasjon()).map(Enum::toString).orElse(null))
                .value(TILBAKE_ETTER_52_UKER, ofNullable(besvarelse.getTilbakeIArbeid()).map(Enum::toString).orElse(null))
                .value(NUS_KODE, ofNullable(UtdanningUtils.mapTilNuskode(besvarelse.getUtdanning())).orElse(null))
                .value(UTDANNING_BESTATT, ofNullable(besvarelse.getUtdanningBestatt()).map(Enum::toString).orElse(null))
                .value(UTDANNING_GODKJENT_NORGE, ofNullable(besvarelse.getUtdanningGodkjent()).map(Enum::toString).orElse(null))
                .value(ANDRE_UTFORDRINGER, ofNullable(besvarelse.getAndreForhold()).map(Enum::toString).orElse(null))
                .execute();

        return id;
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

    static List<TekstForSporsmal> tilTeksterForBesvarelse(String json) {
        try {
            TekstForSporsmal[] teksterForBesvarelse = (new ObjectMapper()).readValue(json, TekstForSporsmal[].class);
            return teksterForBesvarelse != null ? Arrays.asList(teksterForBesvarelse) : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, OrdinaerBrukerRegistreringMapper::map)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .column("*")
                .execute();
    }

    @Override
    public OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, OrdinaerBrukerRegistreringMapper::map)
                .where(WhereClause.equals(AKTOR_ID, aktorId.asString()))
                .orderBy(OrderClause.desc(BRUKER_REGISTRERING_ID))
                .limit(1)
                .column("*")
                .execute();
    }

    @Override
    public SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId) {
        return SqlUtils.select(db, SYKMELDT_REGISTRERING, SykmeldtRegistreringMapper::map)
                .where(WhereClause.equals(AKTOR_ID, aktorId.asString()))
                .orderBy(OrderClause.desc(SYKMELDT_REGISTRERING_ID))
                .limit(1)
                .column("*")
                .execute();
    }

    @Override
    public void lagreReaktiveringForBruker(AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REAKTIVERING_SEQ);
        SqlUtils.insert(db, BRUKER_REAKTIVERING)
                .value(BRUKER_REAKTIVERING_ID, id)
                .value(AKTOR_ID, aktorId.asString())
                .value(REAKTIVERING_DATO, DbConstants.CURRENT_TIMESTAMP)
                .execute();
    }

    @Override
    public long lagre(RegistreringTilstand registreringTilstand) {
        long id = nesteFraSekvens(REGISTRERING_TILSTAND_SEQ);
        SqlUtils.insert(db, "REGISTRERING_TILSTAND")
                .value("ID", id)
                .value("UUID", registreringTilstand.getUuid().toString())
                .value("BRUKER_REGISTRERING_ID", registreringTilstand.getBrukerRegistreringId())
                .value("OPPRETTET", Timestamp.valueOf(registreringTilstand.getOpprettet()))
                .value("SIST_ENDRET", ofNullable(registreringTilstand.getSistEndret())
                        .map(Timestamp::valueOf).orElse(null))
                .value("STATUS", registreringTilstand.getStatus().toString())
                .execute();

        return id;
    }

    @Override
    public void oppdater(RegistreringTilstand registreringTilstand) {
        SqlUtils.update(db, "REGISTRERING_TILSTAND")
                .set("STATUS", registreringTilstand.getStatus())
                .set("SIST_ENDRET", Timestamp.valueOf(registreringTilstand.getSistEndret()))
                .whereEquals("ID", registreringTilstand.getId())
                .execute();
    }

    @Override
    public RegistreringTilstand hentRegistreringTilstand(long id) {
        return SqlUtils.select(db, "REGISTRERING_TILSTAND", RegistreringTilstandMapper::map)
                .where(WhereClause.equals("ID", id))
                .column("*")
                .execute();
    }

    @Override
    public Optional<RegistreringTilstand> finnNesteRegistreringForOverforing() {
        RegistreringTilstand registreringTilstand = SqlUtils.select(db, "REGISTRERING_TILSTAND", RegistreringTilstandMapper::map)
                .where(WhereClause.equals("STATUS", "MOTTATT"))
                .orderBy(OrderClause.asc("OPPRETTET"))
                .limit(1)
                .column("*")
                .execute();

        return ofNullable(registreringTilstand);
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return ((Long)this.db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class)).longValue();
    }

}
