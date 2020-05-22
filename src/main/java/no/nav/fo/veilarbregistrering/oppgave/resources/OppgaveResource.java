package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRouter;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;
import static no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map;

@Component
@Path("/oppgave")
@Produces("application/json")
@Api(value = "OppgaveResource")
public class OppgaveResource {

    private final OppgaveService oppgaveService;
    private final OppgaveRouter oppgaveRouter;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService,
            OppgaveRouter oppgaveRouter
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
        this.oppgaveRouter = oppgaveRouter;
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Oppretter oppgave 'kontakt bruker'")
    public OppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        Oppgave oppgave = oppgaveService.opprettOppgave(bruker, oppgaveDto.getOppgaveType());

        return map(oppgave, oppgaveDto.getOppgaveType());
    }

    @GET
    @Path("/routing")
    @ApiOperation(value = "Henter enhetsId på bakgrunn av siste arbeidsforhold")
    public String hentEnhetsId() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        return oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(bruker)
                .map(Enhetsnr::asString)
                .orElse("0");
    }
}
