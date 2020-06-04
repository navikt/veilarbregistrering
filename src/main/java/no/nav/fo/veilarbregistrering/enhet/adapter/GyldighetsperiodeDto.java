package no.nav.fo.veilarbregistrering.enhet.adapter;

import java.time.LocalDate;

public class GyldighetsperiodeDto {

    private final LocalDate fom;
    private final LocalDate tom;

    public GyldighetsperiodeDto(LocalDate fom, LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }
}