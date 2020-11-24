package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiveringTilstand;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiveringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.Status;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AktiveringTilstandRepositoryImpl implements AktiveringTilstandRepository {

    private final JdbcTemplate db;

    public AktiveringTilstandRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagre(AktiveringTilstand registreringTilstand) {
        long id = nesteFraSekvens("REGISTRERING_TILSTAND_SEQ");
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

    /**
     * Oppdaterer aktiveringTilstand, men sjekker samtidig etter oppdateringer som kan ha skjedd i parallell.
     * @param aktiveringTilstand
     * @throws IllegalStateException dersom sistEndret i databasen er nyere enn den vi forsøker å legge inn.
     */
    @Override
    public void oppdater(AktiveringTilstand aktiveringTilstand) {
        AktiveringTilstand original = hentAktiveringTilstand(aktiveringTilstand.getId());

        if (original.getSistEndret() != null && original.getSistEndret().isAfter(aktiveringTilstand.getSistEndret())) {
            throw new IllegalStateException("RegistreringTilstand hadde allerede blitt oppdatert " +
                    original.getSistEndret().toString() + "Detaljer: " + aktiveringTilstand);
        }

        SqlUtils.update(db, "REGISTRERING_TILSTAND")
                .set("STATUS", aktiveringTilstand.getStatus().name())
                .set("SIST_ENDRET", Timestamp.valueOf(aktiveringTilstand.getSistEndret()))
                .whereEquals("ID", aktiveringTilstand.getId())
                .execute();
    }

    @Override
    public AktiveringTilstand hentAktiveringTilstand(long id) {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND WHERE ID = ?";
        return db.queryForObject(sql, new Object[]{id}, new AktiveringTilstandMapper());
    }

    @Override
    public List<AktiveringTilstand> finnAktiveringTilstandMed(Status status) {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND WHERE STATUS = ?";
        return db.query(sql, new Object[]{status.name()}, new AktiveringTilstandMapper());
    }

    @Override
    public Optional<AktiveringTilstand> finnNesteAktiveringTilstandForOverforing() {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS = ?" +
                " ORDER BY OPPRETTET" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<AktiveringTilstand> registreringTilstand = db.query(sql, new Object[]{"MOTTATT", 0, 1}, new AktiveringTilstandMapper());
        return registreringTilstand.isEmpty() ? Optional.empty() : Optional.of(registreringTilstand.get(0));
    }

    @Override
    public Optional<AktiveringTilstand> finnNesteAktiveringTilstandSomHarFeilet() {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS IN (?, ?)" +
                " ORDER BY OPPRETTET" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<AktiveringTilstand> registreringTilstand = db.query(sql, new Object[]{"MANGLER_ARBEIDSTILLATELSE", "DOD_UTVANDRET_ELLER_FORSVUNNET", 0, 1}, new AktiveringTilstandMapper());
        return registreringTilstand.isEmpty() ? Optional.empty() : Optional.of(registreringTilstand.get(0));
    }

    @Override
    public Optional<AktiveringTilstand> nesteRegistreringKlarForPublisering() {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS IN (?, ?)" +
                " ORDER BY OPPRETTET" +
                " FETCH NEXT ? ROWS ONLY";
        List<AktiveringTilstand> registreringsTilstander = db.query(sql, new Object[]{"OVERFORT_ARENA", "EVENT_PUBLISERT", 1}, new AktiveringTilstandMapper());
        return registreringsTilstander.stream().findFirst();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }
}
