package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdMapper.map;
import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "ArbeidsforholdResource", description = "Tjenester for henting av arbeidsforhold til arbeidssøker.")
public class ArbeidsforholdResource {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public ArbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public ArbeidsforholdDto hentSisteArbeidsforhold() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        return map(flereArbeidsforhold.siste());
    }

}
