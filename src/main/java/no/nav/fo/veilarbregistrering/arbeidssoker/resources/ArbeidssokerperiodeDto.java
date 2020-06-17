package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import java.time.LocalDate;

public class ArbeidssokerperiodeDto {

    private LocalDate fraOgMedDato;
    private LocalDate tilOgMedDato;

    public ArbeidssokerperiodeDto(LocalDate fraOgMedDato, LocalDate tilOgMedDato) {
        this.fraOgMedDato = fraOgMedDato;
        this.tilOgMedDato = tilOgMedDato;
    }

    public LocalDate getFraOgMedDato() {
        return fraOgMedDato;
    }

    public LocalDate getTilOgMedDato() {
        return tilOgMedDato;
    }
}
