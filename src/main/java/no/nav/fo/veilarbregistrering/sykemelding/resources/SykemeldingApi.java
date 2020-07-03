package no.nav.fo.veilarbregistrering.sykemelding.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

@Api(value = "SykemeldingResource")
public interface SykemeldingApi {

    @ApiOperation(value = "Henter sykmeldt informasjon")
    SykmeldtInfoData hentSykmeldtInfoData();
}
