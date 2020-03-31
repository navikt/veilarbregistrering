package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.bruker.Status;

public class RegistreringTilstandDto {

    private long id;
    private Status status;

    public long getId() {
        return this.id;
    }

    public Status getStatus() {
        return this.status;
    }

}
