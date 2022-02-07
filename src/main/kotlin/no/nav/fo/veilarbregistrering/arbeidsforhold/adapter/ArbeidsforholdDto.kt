package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

data class ArbeidsforholdDto (
    val arbeidsgiver: ArbeidsgiverDto? = null,
    val ansettelsesperiode: AnsettelsesperiodeDto? = null,
    val arbeidsavtaler: List<ArbeidsavtaleDto> = emptyList(),
    val navArbeidsforholdId: Int? = null,
)