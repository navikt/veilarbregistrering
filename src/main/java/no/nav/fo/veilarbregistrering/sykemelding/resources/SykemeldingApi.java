package no.nav.fo.veilarbregistrering.sykemelding.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

@Tag(name = "SykemeldingResource")
public interface SykemeldingApi {

    @Operation(summary = "Henter sykmeldt informasjon")
    SykmeldtInfoData hentSykmeldtInfoData();
}
