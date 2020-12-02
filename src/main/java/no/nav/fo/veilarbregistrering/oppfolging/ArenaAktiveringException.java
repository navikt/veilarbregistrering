package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;

public class ArenaAktiveringException extends RuntimeException {

    private final Status status;

    public ArenaAktiveringException(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }
}
