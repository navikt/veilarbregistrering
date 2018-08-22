package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterAlder;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterRegistreringsstatus;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class RegistreringResource {

    private BrukerRegistreringService brukerRegistreringService;
    private ArbeidsforholdService arbeidsforholdService;
    private UserService userService;
    private PepClient pepClient;
    private Provider<HttpServletRequest> requestProvider;

    public RegistreringResource(
            PepClient pepClient,
            UserService userService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService,
            Provider<HttpServletRequest> requestProvider
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.requestProvider = requestProvider;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        StartRegistreringStatus status =  brukerRegistreringService.hentStartRegistreringStatus(userService.getFnr());
        rapporterRegistreringsstatus(status);
        return status;
    }

    @POST
    @Path("/startregistrering")
    @ApiOperation(value = "Starter nyregistrering av arbeidssøker.")
    public BrukerRegistrering registrerBruker(BrukerRegistrering brukerRegistrering) {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        rapporterAlder(userService.getFnr());
        return brukerRegistreringService.registrerBruker(brukerRegistrering, userService.getFnr());
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public BrukerRegistrering hentRegistrering() {
        pepClient.sjekkLeseTilgangTilFnr(getFnr().getFnr());
        return brukerRegistreringService.hentRegistrering(getFnr());
    }

    @POST
    @Path("/startreaktivering")
    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    public void reaktivering() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        rapporterAlder(userService.getFnr());
        brukerRegistreringService.reaktiverBruker(userService.getFnr());
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return arbeidsforholdService.hentSisteArbeidsforhold(userService.getFnr());
    }

    private Fnr getFnr() {
        return new Fnr(requestProvider.get().getParameter("fnr"));
    }
}
