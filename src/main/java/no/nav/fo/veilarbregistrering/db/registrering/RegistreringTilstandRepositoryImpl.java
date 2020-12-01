package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RegistreringTilstandRepositoryImpl implements RegistreringTilstandRepository {

    private final JdbcTemplate db;

    public RegistreringTilstandRepositoryImpl(JdbcTemplate db) {
        this.db = db;
    }

    @Override
    public long lagre(RegistreringTilstand registreringTilstand) {
        long id = nesteFraSekvens("REGISTRERING_TILSTAND_SEQ");
        SqlUtils.insert(db, "REGISTRERING_TILSTAND")
                .value("ID", id)
                .value("BRUKER_REGISTRERING_ID", registreringTilstand.getBrukerRegistreringId())
                .value("OPPRETTET", Timestamp.valueOf(registreringTilstand.getOpprettet()))
                .value("SIST_ENDRET", ofNullable(registreringTilstand.getSistEndret())
                        .map(Timestamp::valueOf).orElse(null))
                .value("STATUS", registreringTilstand.getStatus().toString())
                .execute();

        return id;
    }

    /**
     * Oppdaterer registreringtilstand, men sjekker samtidig etter oppdateringer som kan ha skjedd i parallell.
     * @param registreringTilstand
     * @throws IllegalStateException dersom sistEndret i databasen er nyere enn den vi forsøker å legge inn.
     */
    @Override
    public void oppdater(RegistreringTilstand registreringTilstand) {
        RegistreringTilstand original = hentRegistreringTilstand(registreringTilstand.getId());

        if (original.getSistEndret() != null && original.getSistEndret().isAfter(registreringTilstand.getSistEndret())) {
            throw new IllegalStateException("RegistreringTilstand hadde allerede blitt oppdatert " +
                    original.getSistEndret().toString() + "Detaljer: " + registreringTilstand);
        }

        SqlUtils.update(db, "REGISTRERING_TILSTAND")
                .set("STATUS", registreringTilstand.getStatus().name())
                .set("SIST_ENDRET", Timestamp.valueOf(registreringTilstand.getSistEndret()))
                .whereEquals("ID", registreringTilstand.getId())
                .execute();
    }

    @Override
    public RegistreringTilstand hentRegistreringTilstand(long id) {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND WHERE ID = ?";
        return db.queryForObject(sql, new Object[]{id}, new RegistreringTilstandMapper());
    }

    @Override
    public List<RegistreringTilstand> finnRegistreringTilstandMed(Status status) {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND WHERE STATUS = ?";
        return db.query(sql, new Object[]{status.name()}, new RegistreringTilstandMapper());
    }

    @Override
    public Optional<RegistreringTilstand> finnNesteRegistreringTilstandForOverforing() {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS = ?" +
                " ORDER BY OPPRETTET" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<RegistreringTilstand> registreringTilstand = db.query(sql, new Object[]{"MOTTATT", 0, 1}, new RegistreringTilstandMapper());
        return registreringTilstand.isEmpty() ? Optional.empty() : Optional.of(registreringTilstand.get(0));
    }

    @Override
    public Optional<RegistreringTilstand> finnNesteRegistreringTilstandSomHarFeilet() {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS IN (?, ?)" +
                " ORDER BY OPPRETTET" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<RegistreringTilstand> registreringTilstand = db.query(sql, new Object[]{"MANGLER_ARBEIDSTILLATELSE", "DOD_UTVANDRET_ELLER_FORSVUNNET", 0, 1}, new RegistreringTilstandMapper());
        return registreringTilstand.isEmpty() ? Optional.empty() : Optional.of(registreringTilstand.get(0));
    }

    @Override
    public Optional<RegistreringTilstand> finnNesteRegistreringTilstandMed(Status status) {
        String sql = "SELECT * FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS = ?" +
                " ORDER BY OPPRETTET" +
                " FETCH NEXT ? ROWS ONLY";
        List<RegistreringTilstand> registreringsTilstander = db.query(sql, new Object[]{status.name(), 1}, new RegistreringTilstandMapper());
        return registreringsTilstander.stream().findFirst();
    }

    @Override
    public int hentAntall(Status status) {
        String sql = "SELECT COUNT(1) FROM REGISTRERING_TILSTAND" +
                " WHERE STATUS = ?";

        return db.queryForObject(sql, new Object[]{ status.name() }, Integer.class);
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return db.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }
}
