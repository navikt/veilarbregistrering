package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

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

    public RegistreringResource(
            PepClient pepClient,
            UserService userService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.brukerRegistreringService = brukerRegistreringService;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        StartRegistreringStatus status = brukerRegistreringService.hentStartRegistreringStatus(userService.getFnr());
        rapporterRegistreringsstatus(status);
        return status;
    }

    @POST
    @Path("/startregistrering")
    @ApiOperation(value = "Starter nyregistrering av arbeidssøker.")
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        OrdinaerBrukerRegistrering registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, userService.getFnr());
        rapporterAlder(userService.getFnr());
        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public ProfilertBrukerRegistrering hentProfilertRegistrering() {
        String fnr = userService.getFnrFromUrl();
        pepClient.sjekkLeseTilgangTilFnr(fnr);
        return brukerRegistreringService.hentProfilertBrukerRegistrering(new Fnr(fnr));
    }

    @POST
    @Path("/startreaktivering")
    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    public void reaktivering() {
        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        brukerRegistreringService.reaktiverBruker(userService.getFnr());
        rapporterAlder(userService.getFnr());
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return arbeidsforholdService.hentSisteArbeidsforhold(userService.getFnr());
    }

    @POST
    @Path("/startregistrersykmeldt")
    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    public void registrerSykmeldt() {
        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        brukerRegistreringService.registrerSykmeldt(userService.getFnr());
    }

}
