package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "ArbeidsforholdResource")
interface ArbeidsforholdApi {
    @Operation(summary = "Henter informasjon om brukers siste arbeidsforhold.")
    fun hentSisteArbeidsforhold(): ArbeidsforholdDto?
}