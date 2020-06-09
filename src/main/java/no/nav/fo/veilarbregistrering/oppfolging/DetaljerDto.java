package no.nav.fo.veilarbregistrering.oppfolging;

public class DetaljerDto {

    private String detaljertType;
    private String feilMelding;
    private String stackTrace;

    public DetaljerDto() {
    }

    public String getDetaljertType() {
        return detaljertType;
    }

    public String getFeilMelding() {
        return feilMelding;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
