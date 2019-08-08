package no.nav.fo.veilarbregistrering.sykemelding.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class SykemeldingResource {

    private final BrukerRegistreringService brukerRegistreringService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final AktorService aktorService;

    public SykemeldingResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.aktorService=aktorService;
    }

    @GET
    @Path("/sykmeldtinfodata")
    @ApiOperation(value = "Henter sykmeldt informasjon")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        //FIXME: Denne metoden bør flyttes til en egen SykemeldingService.
        return brukerRegistreringService.hentSykmeldtInfoData(bruker.getFoedselsnummer());
    }

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }



}
