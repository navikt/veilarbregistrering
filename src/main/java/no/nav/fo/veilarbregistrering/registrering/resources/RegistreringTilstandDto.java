package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.bruker.Status;

public class RegistreringTilstandDto {

    private long id;
    private Status status;

    public static RegistreringTilstandDto of(long id, Status status) {
        return new RegistreringTilstandDto(id, status);
    }

    private RegistreringTilstandDto(long id, Status status)  {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return this.id;
    }

    public Status getStatus() {
        return this.status;
    }

}
