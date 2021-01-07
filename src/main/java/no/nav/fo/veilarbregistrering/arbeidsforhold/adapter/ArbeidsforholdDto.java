package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import java.util.List;
import java.util.Objects;

class ArbeidsforholdDto {

    private ArbeidsgiverDto arbeidsgiver;
    private AnsettelsesperiodeDto ansettelsesperiode;
    private List<ArbeidsavtaleDto> arbeidsavtaler;
    private Integer navArbeidsforholdId;

    ArbeidsforholdDto() {
    }

    ArbeidsgiverDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    void setArbeidsgiver(ArbeidsgiverDto arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
    }

    AnsettelsesperiodeDto getAnsettelsesperiode() {
        return ansettelsesperiode;
    }

    void setAnsettelsesperiode(AnsettelsesperiodeDto ansettelsesperiode) {
        this.ansettelsesperiode = ansettelsesperiode;
    }

    List<ArbeidsavtaleDto> getArbeidsavtaler() {
        return arbeidsavtaler;
    }

    void setArbeidsavtaler(List<ArbeidsavtaleDto> arbeidsavtaler) {
        this.arbeidsavtaler = arbeidsavtaler;
    }

    Integer getNavArbeidsforholdId() {
        return navArbeidsforholdId;
    }

    void setNavArbeidsforholdId(Integer navArbeidsforholdId) {
        this.navArbeidsforholdId = navArbeidsforholdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbeidsforholdDto that = (ArbeidsforholdDto) o;
        return Objects.equals(arbeidsgiver, that.arbeidsgiver) &&
                Objects.equals(ansettelsesperiode, that.ansettelsesperiode) &&
                Objects.equals(arbeidsavtaler, that.arbeidsavtaler) &&
                Objects.equals(navArbeidsforholdId, that.navArbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiver, ansettelsesperiode, arbeidsavtaler, navArbeidsforholdId);
    }
}

