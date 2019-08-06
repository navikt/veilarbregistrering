package no.nav.fo.veilarbregistrering.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.utils.UtdanningUtils;
import no.nav.sbl.sql.DbConstants;
import no.nav.sbl.sql.SqlUtils;
import no.nav.sbl.sql.order.OrderClause;
import no.nav.sbl.sql.where.WhereClause;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

public class ArbeidssokerregistreringRepository {

    private JdbcTemplate db;

    private final static String SYKMELDT_REGISTRERING_SEQ = "SYKMELDT_REGISTRERING_SEQ";
    private final static String SYKMELDT_REGISTRERING_ID = "SYKMELDT_REGISTRERING_ID";
    private final static String SYKMELDT_REGISTRERING = "SYKMELDT_REGISTRERING";
    private final static String FREMTIDIG_SITUASJON = "FREMTIDIG_SITUASJON";
    private final static String TILBAKE_ETTER_52_UKER = "TILBAKE_ETTER_52_UKER";

    private final static String MANUELL_REGISTRERING_SEQ = "MANUELL_REGISTRERING_SEQ";
    private final static String BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ";
    private final static String BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ";
    private final static String BRUKER_REGISTRERING = "BRUKER_REGISTRERING";
    private final static String BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING";
    private final static String MANUELL_REGISTRERING = "MANUELL_REGISTRERING";
    private final static String MANUELL_REGISTRERING_ID = "MANUELL_REGISTRERING_ID";
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

    private final static String REGISTRERING_ID = "REGISTRERING_ID";
    private final static String BRUKER_REGISTRERING_TYPE = "BRUKER_REGISTRERING_TYPE";
    private final static String VEILEDER_IDENT = "VEILEDER_IDENT";
    private final static String VEILEDER_ENHET_ID = "VEILEDER_ENHET_ID";

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

    public OrdinaerBrukerRegistrering lagreOrdinaerBruker(OrdinaerBrukerRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        Stilling stilling = bruker.getSisteStilling();
        String teksterForBesvarelse = tilJson(bruker.getTeksterForBesvarelse());

        SqlUtils.insert(db, BRUKER_REGISTRERING)
                .value(BRUKER_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
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

    public long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId) {
        long id = nesteFraSekvens(SYKMELDT_REGISTRERING_SEQ);
        Besvarelse besvarelse = bruker.getBesvarelse();
        String teksterForBesvarelse = tilJson(bruker.getTeksterForBesvarelse());

        SqlUtils.insert(db, SYKMELDT_REGISTRERING)
                .value(SYKMELDT_REGISTRERING_ID, id)
                .value(AKTOR_ID, aktorId.getAktorId())
                .value(OPPRETTET_DATO, DbConstants.CURRENT_TIMESTAMP)
                .value(TEKSTER_FOR_BESVARELSE, teksterForBesvarelse)
                // Besvarelse
                .value(FREMTIDIG_SITUASJON, ofNullable(besvarelse.getFremtidigSituasjon()).isPresent() ? besvarelse.getFremtidigSituasjon().toString() : null)
                .value(TILBAKE_ETTER_52_UKER, ofNullable(besvarelse.getTilbakeIArbeid()).isPresent() ? besvarelse.getTilbakeIArbeid().toString() : null)
                .value(NUS_KODE, ofNullable(UtdanningUtils.mapTilNuskode(besvarelse.getUtdanning())).orElse(null))
                .value(UTDANNING_BESTATT, ofNullable(besvarelse.getUtdanningBestatt()).isPresent() ? besvarelse.getUtdanningBestatt().toString() : null)
                .value(UTDANNING_GODKJENT_NORGE, ofNullable(besvarelse.getUtdanningGodkjent()).isPresent() ? besvarelse.getUtdanningGodkjent().toString() : null)
                .value(ANDRE_UTFORDRINGER, ofNullable(besvarelse.getAndreForhold()).isPresent() ? besvarelse.getAndreForhold().toString() : null)
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

    private static List<TekstForSporsmal> tilTeksterForBesvarelse(String json) {
        try {
            TekstForSporsmal[] teksterForBesvarelse = (new ObjectMapper()).readValue(json, TekstForSporsmal[].class);
            return teksterForBesvarelse != null ? Arrays.asList(teksterForBesvarelse) : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, ArbeidssokerregistreringRepository::brukerRegistreringMapper)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .column("*")
                .execute();
    }

    public Profilering hentProfileringForId(long brukerregistreringId){
        return SqlUtils.select(db, BRUKER_PROFILERING, ArbeidssokerregistreringRepository::brukerProfileringMapper)
                .where(WhereClause.equals(BRUKER_REGISTRERING_ID, brukerregistreringId))
                .limit(3)
                .column("*")
                .execute();
    }

    public OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId) {
        return SqlUtils.select(db, BRUKER_REGISTRERING, ArbeidssokerregistreringRepository::brukerRegistreringMapper)
                .where(WhereClause.equals(AKTOR_ID, aktorId.getAktorId()))
                .orderBy(OrderClause.desc(BRUKER_REGISTRERING_ID))
                .limit(1)
                .column("*")
                .execute();
    }

    public SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId) {
        return SqlUtils.select(db, SYKMELDT_REGISTRERING, ArbeidssokerregistreringRepository::sykmeldtRegistreringMapper)
                .where(WhereClause.equals(AKTOR_ID, aktorId.getAktorId()))
                .orderBy(OrderClause.desc(SYKMELDT_REGISTRERING_ID))
                .limit(1)
                .column("*")
                .execute();
    }

    public OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringMedProfileringForAktorId(AktorId aktorId) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = hentOrdinaerBrukerregistreringForAktorId(aktorId);
        if (ordinaerBrukerRegistrering == null) {
            return null;
        }
        Profilering profilering = hentProfileringForId(ordinaerBrukerRegistrering.getId());
        ordinaerBrukerRegistrering.setProfilering(profilering);
        return ordinaerBrukerRegistrering;
    }

    public long lagreManuellRegistrering(ManuellRegistrering manuellRegistrering) {
        long id = nesteFraSekvens(MANUELL_REGISTRERING_SEQ);
        SqlUtils.insert(db, MANUELL_REGISTRERING)
                .value(MANUELL_REGISTRERING_ID, id)
                .value(REGISTRERING_ID, manuellRegistrering.getRegistreringId())
                .value(BRUKER_REGISTRERING_TYPE, manuellRegistrering.getBrukerRegistreringType().toString())
                .value(VEILEDER_IDENT, manuellRegistrering.getVeilederIdent())
                .value(VEILEDER_ENHET_ID, manuellRegistrering.getVeilederEnhetId())
                .execute();
        return id;
    }

    public ManuellRegistrering hentManuellRegistrering(long registreringId, BrukerRegistreringType brukerRegistreringType) {
        return SqlUtils.select(db, MANUELL_REGISTRERING, ArbeidssokerregistreringRepository::manuellRegistreringMapper)
                .where(WhereClause.equals(REGISTRERING_ID, registreringId)
                        .and(WhereClause.equals(BRUKER_REGISTRERING_TYPE, brukerRegistreringType.toString())))
                .limit(1)
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
    private static Profilering brukerProfileringMapper(ResultSet rs) {

        Profilering profilering = new Profilering();

        do {
            switch (rs.getString(PROFILERING_TYPE)){
                case ALDER:
                    profilering.setAlder(rs.getInt(VERDI));
                    break;
                case ARB_6_AV_SISTE_12_MND:
                    profilering.setJobbetSammenhengendeSeksAvTolvSisteManeder(rs.getBoolean(VERDI));
                    break;
                case RESULTAT_PROFILERING:
                    profilering.setInnsatsgruppe(Innsatsgruppe.tilInnsatsgruppe(rs.getString(VERDI)));
                    break;
            }

        } while (rs.next());

        return profilering;

    }

    @SneakyThrows
    private static ManuellRegistrering manuellRegistreringMapper(ResultSet rs) {
        return new ManuellRegistrering()
                .setId(rs.getLong(MANUELL_REGISTRERING_ID))
                .setRegistreringId(rs.getLong(REGISTRERING_ID))
                .setBrukerRegistreringType(BrukerRegistreringType.valueOf(rs.getString(BRUKER_REGISTRERING_TYPE)))
                .setVeilederIdent(rs.getString(VEILEDER_IDENT))
                .setVeilederEnhetId(rs.getString(VEILEDER_ENHET_ID));
    }

    @SneakyThrows
    private static OrdinaerBrukerRegistrering brukerRegistreringMapper(ResultSet rs) {
        return new OrdinaerBrukerRegistrering()
                .setId(rs.getLong(BRUKER_REGISTRERING_ID))
                .setOpprettetDato(rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime())
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

    @SneakyThrows
    private static SykmeldtRegistrering sykmeldtRegistreringMapper(ResultSet rs) {
        return new SykmeldtRegistrering()
                .setId(rs.getLong(SYKMELDT_REGISTRERING_ID))
                .setOpprettetDato(rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime())
                .setTeksterForBesvarelse(tilTeksterForBesvarelse(rs.getString(TEKSTER_FOR_BESVARELSE)))
                .setBesvarelse(new Besvarelse()
                        .setFremtidigSituasjon(
                                ofNullable(rs.getString(FREMTIDIG_SITUASJON)).isPresent()
                                ? FremtidigSituasjonSvar.valueOf(rs.getString(FREMTIDIG_SITUASJON))
                                        : null
                        )
                        .setTilbakeIArbeid(
                                ofNullable(rs.getString(TILBAKE_ETTER_52_UKER)).isPresent()
                                ? TilbakeIArbeidSvar.valueOf(rs.getString(TILBAKE_ETTER_52_UKER))
                                        : null
                        )
                        .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)))
                        .setUtdanningBestatt(
                                ofNullable(rs.getString(UTDANNING_BESTATT)).isPresent()
                                        ? UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT))
                                        : null
                                )
                        .setUtdanningGodkjent(
                                ofNullable(rs.getString(UTDANNING_GODKJENT_NORGE)).isPresent()
                                ? UtdanningGodkjentSvar.valueOf(rs.getString(UTDANNING_GODKJENT_NORGE))
                                        : null
                        )
                        .setAndreForhold(
                                ofNullable(rs.getString(ANDRE_UTFORDRINGER)).isPresent()
                                ? AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER))
                                        : null
                        )
                );
    }
}
