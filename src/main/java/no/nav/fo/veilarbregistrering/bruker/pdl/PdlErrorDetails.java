package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlErrorDetails {

    private final String type;
    private final String cause;
    private final String policy;

    public PdlErrorDetails(String type, String cause, String policy) {
        this.type = type;
        this.cause = cause;
        this.policy = policy;
    }

    public String getType() {
        return type;
    }

    public String getCause() {
        return cause;
    }

    public String getPolicy() {
        return policy;
    }
}
