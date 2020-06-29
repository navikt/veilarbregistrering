package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

public class ArbeidssokerperiodeDto {

    private String fraOgMedDato;
    private String tilOgMedDato;

    public ArbeidssokerperiodeDto(String fraOgMedDato, String tilOgMedDato) {
        this.fraOgMedDato = fraOgMedDato;
        this.tilOgMedDato = tilOgMedDato;
    }

    public String getFraOgMedDato() {
        return fraOgMedDato;
    }

    public String getTilOgMedDato() {
        return tilOgMedDato;
    }
}
