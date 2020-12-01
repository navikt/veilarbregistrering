package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.time.LocalDateTime;

/**
 * RegistreringTilstand representerer tilstanden til en BrukerRegistrering på vei mot Arena.
 * Status sier noe om hvor langt registreringen har kommet ift. overføring til Arena og evt. response derfra.
 */
public class RegistreringTilstand {

    private long id;
    private long brukerRegistreringId;
    private LocalDateTime opprettet;
    private LocalDateTime sistEndret;
    private Status status;

    public static RegistreringTilstand of(long id, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        return new RegistreringTilstand(id, brukerRegistreringId, opprettet, sistEndret, status);
    }

    public static RegistreringTilstand medStatus(Status status, long brukerRegistreringId) {
        return new RegistreringTilstand(-1L, brukerRegistreringId, LocalDateTime.now(), null, status);
    }

    private RegistreringTilstand(long id, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        this.id = id;
        this.brukerRegistreringId = brukerRegistreringId;
        this.opprettet = opprettet;
        this.sistEndret = sistEndret;
        this.status = status;
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
        return new RegistreringTilstand(this.id, this.brukerRegistreringId, this.opprettet, LocalDateTime.now(), status);
    }

    @Override
    public String toString() {
        return "RegistreringTilstand{" +
                ", id=" + id +
                ", brukerRegistreringId=" + brukerRegistreringId +
                ", opprettet=" + opprettet +
                ", sistEndret=" + sistEndret +
                ", status=" + status +
                '}';
    }
}
