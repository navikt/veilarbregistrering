package no.nav.fo.veilarbregistrering.registrering.formidling;

import java.time.LocalDateTime;

/**
 * RegistreringTilstand representerer tilstanden til en BrukerRegistrering på vei mot Arena.
 * Status sier noe om hvor langt registreringen har kommet ift. overføring til Arena og evt. response derfra.
 */
public class RegistreringFormidling {

    private long id;
    private long brukerRegistreringId;
    private LocalDateTime opprettet;
    private LocalDateTime sistEndret;
    private Status status;

    public static RegistreringFormidling of(long id, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
        return new RegistreringFormidling(id, brukerRegistreringId, opprettet, sistEndret, status);
    }

    public static RegistreringFormidling medStatus(Status status, long brukerRegistreringId) {
        return new RegistreringFormidling(-1L, brukerRegistreringId, LocalDateTime.now(), null, status);
    }

    private RegistreringFormidling(long id, long brukerRegistreringId, LocalDateTime opprettet, LocalDateTime sistEndret, Status status) {
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

    public RegistreringFormidling oppdaterStatus(Status status) {
        return new RegistreringFormidling(this.id, this.brukerRegistreringId, this.opprettet, LocalDateTime.now(), status);
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
