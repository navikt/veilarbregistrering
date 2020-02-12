package no.nav.fo.veilarbregistrering.sykemelding.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "SykemeldingResource", description = "Tjenester for uthenting av maksdato for arbeidssøker.")
public class SykemeldingResource {

    private final SykemeldingService sykemeldingService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public SykemeldingResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            SykemeldingService sykemeldingService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.sykemeldingService = sykemeldingService;
    }

    @GET
    @Path("/sykmeldtinfodata")
    @ApiOperation(value = "Henter sykmeldt informasjon")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);

        return sykemeldingService.hentSykmeldtInfoData(bruker.getFoedselsnummer());
    }
}
