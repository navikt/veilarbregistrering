package no.nav.fo.veilarbregistrering.registrering.tilstand.resources;

import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;

public class RegistreringTilstandDto {

    private final long id;
    private final Status status;

    public static RegistreringTilstandDto of(long id, Status status) {
        return new RegistreringTilstandDto(id, status);
    }

    public static RegistreringTilstandDto of(String id, Status status) {
        return new RegistreringTilstandDto(Long.parseLong(id), status);
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
