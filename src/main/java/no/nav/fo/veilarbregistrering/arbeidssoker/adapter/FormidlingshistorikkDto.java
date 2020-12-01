package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import java.time.LocalDate;
import java.util.Objects;

public class FormidlingshistorikkDto {

    private final String formidlingsgruppeKode;
    private final String modDato;
    private final LocalDate fraDato;
    private final LocalDate tilDato;


    public FormidlingshistorikkDto(String formidlingsgruppeKode, String modDato, LocalDate fraDato, LocalDate tilDato) {
        this.formidlingsgruppeKode = formidlingsgruppeKode;
        this.modDato = modDato;
        this.fraDato = fraDato;
        this.tilDato = tilDato;
    }

    public String getFormidlingsgruppeKode() {
        return formidlingsgruppeKode;
    }

    public String getModDato() {
        return modDato;
    }

    public LocalDate getFraDato() {
        return fraDato;
    }

    public LocalDate getTilDato() {
        return tilDato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormidlingshistorikkDto that = (FormidlingshistorikkDto) o;
        return Objects.equals(formidlingsgruppeKode, that.formidlingsgruppeKode) &&
                Objects.equals(modDato, that.modDato) &&
                Objects.equals(fraDato, that.fraDato) &&
                Objects.equals(tilDato, that.tilDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formidlingsgruppeKode, modDato, fraDato, tilDato);
    }
}
