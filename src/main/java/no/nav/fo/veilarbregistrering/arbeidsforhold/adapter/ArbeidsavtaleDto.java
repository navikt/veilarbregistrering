package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import java.util.Objects;

class ArbeidsavtaleDto {

    private String yrke;

    ArbeidsavtaleDto() {}

    String getYrke() {
        return yrke;
    }

    void setYrke(String yrke) {
        this.yrke = yrke;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbeidsavtaleDto that = (ArbeidsavtaleDto) o;
        return Objects.equals(yrke, that.yrke);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yrke);
    }
}
