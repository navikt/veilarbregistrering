package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RegistreringTilstand representerer tilstanden til en BrukerRegistrering.
 * Status sier noe om hvor langt registreringen har kommet ift. overføring til Arena og evt. response derfra.
 */
public class RegistreringTilstand {

    private long id;
    private UUID uuid;
    private long brukerRegistreringId;
    private LocalDateTime opprettet;
    private LocalDateTime sistEndret;
    private Status status;

    public static RegistreringTilstand ofArenaOk(long brukerRegistreringId) {
        return new RegistreringTilstand(-1L, UUID.randomUUID(), brukerRegistreringId, LocalDateTime.now(), null, Status.ARENA_OK);
    }

    public static RegistreringTilstand ofMottattRegistrering(long brukerRegistreringId) {
        return new RegistreringTilstand(-1L, UUID.randomUUID(), brukerRegistreringId, LocalDateTime.now(), null, Status.MOTTATT);
    }

    public static RegistreringTilstand fromDb(long id, UUID uuid, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        return new RegistreringTilstand(id, uuid, brukerRegistreringId, opprettet, sistEndret, status);
    }

    private RegistreringTilstand(long id, UUID uuid, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        this.id = id;
        this.uuid = uuid;
        this.brukerRegistreringId = brukerRegistreringId;
        this.opprettet = opprettet;
        this.sistEndret = sistEndret;
        this.status = status;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getBrukerRegistreringId() {
        return brukerRegistreringId;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public LocalDateTime getSistEndret() {
        return sistEndret;
    }

    public Status getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public RegistreringTilstand oppdaterStatus(Status status) {
        this.status = status;
        this.sistEndret = LocalDateTime.now();
        return this;
    }

    @Override
    public String toString() {
        return "RegistreringTilstand{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", brukerRegistreringId=" + brukerRegistreringId +
                ", opprettet=" + opprettet +
                ", sistEndret=" + sistEndret +
                ", status=" + status +
                '}';
    }
}
