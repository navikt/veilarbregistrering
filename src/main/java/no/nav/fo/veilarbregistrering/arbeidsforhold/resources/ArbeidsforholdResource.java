package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdMapper.map;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "ArbeidsforholdResource", description = "Tjenester for henting av arbeidsforhold til arbeidssøker.")
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
    public ArbeidsforholdDto hentSisteArbeidsforhold() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        return map(flereArbeidsforhold.siste());
    }

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }

}
