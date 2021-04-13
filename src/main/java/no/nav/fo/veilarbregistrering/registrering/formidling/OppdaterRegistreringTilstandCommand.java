package no.nav.fo.veilarbregistrering.registrering.formidling;

public class OppdaterRegistreringTilstandCommand {

    private final long id;
    private final Status status;

    public static OppdaterRegistreringTilstandCommand of(long id, Status status) {
        return new OppdaterRegistreringTilstandCommand(id, status);
    }

    public static OppdaterRegistreringTilstandCommand of(String id, Status status) {
        return new OppdaterRegistreringTilstandCommand(Long.parseLong(id), status);
    }

    private OppdaterRegistreringTilstandCommand(long id, Status status)  {
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
