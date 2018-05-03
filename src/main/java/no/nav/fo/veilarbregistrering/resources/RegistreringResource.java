package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
@Slf4j
@Api
public class RegistreringResource {

    private BrukerRegistreringService brukerRegistreringService;
    private ArbeidsforholdService arbeidsforholdService;
    private UserService userService;
    private PepClient pepClient;

    public RegistreringResource(
            PepClient pepClient,
            UserService userService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService) {

        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.brukerRegistreringService = brukerRegistreringService;
    }

    @GET
    @Path("/startregistrering")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return brukerRegistreringService.hentStartRegistreringStatus(userService.getFnr());
    }

    @POST
    @Path("/startregistrering")
    public BrukerRegistrering registrerBruker(BrukerRegistrering brukerRegistrering) {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return brukerRegistreringService.registrerBruker(brukerRegistrering, userService.getFnr());
    }

    @GET
    @Path("/sistearbeidsforhold")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return arbeidsforholdService.hentSisteArbeidsforhold(userService.getFnr());
    }

}
