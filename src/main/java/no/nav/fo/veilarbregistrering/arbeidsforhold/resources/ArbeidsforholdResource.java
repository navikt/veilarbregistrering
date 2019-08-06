package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class ArbeidsforholdResource {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final AktorService aktorService;

    public ArbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway,
            AktorService aktorService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.aktorService=aktorService;
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        return arbeidsforholdGateway.hentSisteArbeidsforhold(bruker.getFoedselsnummer());
    }

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }

}
