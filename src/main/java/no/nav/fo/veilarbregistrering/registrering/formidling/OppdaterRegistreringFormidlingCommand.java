package no.nav.fo.veilarbregistrering.registrering.formidling;

public class OppdaterRegistreringFormidlingCommand {

    private final long id;
    private final Status status;

    public static OppdaterRegistreringFormidlingCommand of(long id, Status status) {
        return new OppdaterRegistreringFormidlingCommand(id, status);
    }

    public static OppdaterRegistreringFormidlingCommand of(String id, Status status) {
        return new OppdaterRegistreringFormidlingCommand(Long.parseLong(id), status);
    }

    private OppdaterRegistreringFormidlingCommand(long id, Status status)  {
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
