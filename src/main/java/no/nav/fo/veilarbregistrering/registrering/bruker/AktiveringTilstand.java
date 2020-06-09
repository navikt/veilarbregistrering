package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AktiveringTilstand representerer tilstanden til en BrukerRegistrering på vei mot Arena.
 * Status sier noe om hvor langt registreringen har kommet ift. overføring til Arena og evt. response derfra.
 */
public class AktiveringTilstand {

    private long id;
    private UUID uuid;
    private long brukerRegistreringId;
    private LocalDateTime opprettet;
    private LocalDateTime sistEndret;
    private Status status;

    public static AktiveringTilstand ofArenaOk(long brukerRegistreringId) {
        return new AktiveringTilstand(-1L, UUID.randomUUID(), brukerRegistreringId, LocalDateTime.now(), null, Status.ARENA_OK);
    }

    public static AktiveringTilstand ofMottattRegistrering(long brukerRegistreringId) {
        return new AktiveringTilstand(-1L, UUID.randomUUID(), brukerRegistreringId, LocalDateTime.now(), null, Status.MOTTATT);
    }

    public static AktiveringTilstand of(long id, UUID uuid, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        return new AktiveringTilstand(id, uuid, brukerRegistreringId, opprettet, sistEndret, status);
    }

    private AktiveringTilstand(long id, UUID uuid, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
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

    public AktiveringTilstand oppdaterStatus(Status status) {
        return new AktiveringTilstand(this.id, this.uuid, this.brukerRegistreringId, this.opprettet, LocalDateTime.now(), status);
    }

    @Override
    public String toString() {
        return "AktiveringTilstand{" +
                "uuid=" + uuid +
                ", id=" + id +
                ", brukerRegistreringId=" + brukerRegistreringId +
                ", opprettet=" + opprettet +
                ", sistEndret=" + sistEndret +
                ", status=" + status +
                '}';
    }
}
