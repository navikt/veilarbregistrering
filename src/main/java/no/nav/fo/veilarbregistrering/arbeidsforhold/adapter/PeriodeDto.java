package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import java.util.Objects;

class PeriodeDto {

    private String fom;
    private String tom;

    PeriodeDto() {}

    String getFom() {
        return fom;
    }

    void setFom(String fom) {
        this.fom = fom;
    }

    String getTom() {
        return tom;
    }

    void setTom(String tom) {
        this.tom = tom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodeDto that = (PeriodeDto) o;
        return Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fom, tom);
    }
}
