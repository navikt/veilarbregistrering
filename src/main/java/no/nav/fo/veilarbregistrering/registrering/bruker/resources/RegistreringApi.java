package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;

@Api(value = "RegistreringResource")
public interface RegistreringApi {

    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    StartRegistreringStatusDto hentStartRegistreringStatus();

    @ApiOperation(value = "Starter nyregistrering av arbeidssøker.")
    OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering);

    @ApiOperation(value = "Henter siste registrering av bruker.")
    BrukerRegistreringWrapper hentRegistrering();

    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    void reaktivering();

    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering);
}