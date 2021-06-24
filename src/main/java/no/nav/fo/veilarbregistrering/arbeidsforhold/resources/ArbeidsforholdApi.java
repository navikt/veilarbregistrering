package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "ArbeidsforholdResource")
public interface ArbeidsforholdApi {

    @Operation(summary = "Henter informasjon om brukers siste arbeidsforhold.")
    ArbeidsforholdDto hentSisteArbeidsforhold();
}
