package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import java.util.Objects;

class AnsettelsesperiodeDto {

    private PeriodeDto periode;

    AnsettelsesperiodeDto() {
    }

    PeriodeDto getPeriode() {
        return periode;
    }

    void setPeriode(PeriodeDto periode) {
        this.periode = periode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnsettelsesperiodeDto that = (AnsettelsesperiodeDto) o;
        return Objects.equals(periode, that.periode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periode);
    }
}
