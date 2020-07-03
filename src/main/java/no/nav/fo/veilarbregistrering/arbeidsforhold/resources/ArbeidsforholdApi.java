package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "ArbeidsforholdResource")
public interface ArbeidsforholdApi {

    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    ArbeidsforholdDto hentSisteArbeidsforhold();
}
