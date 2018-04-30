package no.nav.fo.veilarbregistrering.resources;

import no.nav.apiapp.security.PepClient;
import no.nav.apiapp.util.SubjectUtils;
import no.nav.fo.veilarbregistrering.config.PepConfig;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
public class RegistreringResource {

    @Inject
    private BrukerRegistreringService brukerRegistreringService;

    @Inject
    private PepClient pepClient;

    @GET
    @Path("/startregistrering")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        pepClient.sjekkLeseTilgangTilFnr(getFnr());
        return brukerRegistreringService.hentStartRegistreringStatus(getFnr());
    }

    @POST
    @Path("/startregistrering")
    public BrukerRegistrering registrerBruker(BrukerRegistrering brukerRegistrering) {
        pepClient.sjekkLeseTilgangTilFnr(getFnr());
        return brukerRegistreringService.registrerBruker(brukerRegistrering, getFnr());
    }

    @GET
    @Path("/sistearbeidsforhold")
    public Arbeidsforhold hentArbeidsforholdet() {
        pepClient.sjekkLeseTilgangTilFnr(getFnr());
        return null;
    }

    private String getFnr() {
        return SubjectUtils.getUserId().orElseThrow(IllegalArgumentException::new);
    }

}
