package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/oppgave")
@Produces("application/json")
@Api(value = "OppgaveResource")
public class OppgaveResource {

    private final OppgaveGateway oppgaveGateway;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final AktorService aktorService;

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveGateway oppgaveGateway,
            AktorService aktorService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveGateway = oppgaveGateway;
        this.aktorService = aktorService;
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Oppretter oppgave 'kontakt bruker'")
    public void opprettOppgave() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(bruker);

        oppgaveGateway.opprettOppgave(bruker.getAktoerId());
    }

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(() -> aktorService.getAktorId(fnr)
                        .orElseThrow(() -> new Feil(FeilType.FINNES_IKKE)));
    }
}
