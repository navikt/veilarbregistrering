package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;
import static no.nav.fo.veilarbregistrering.bruker.UserService.Kilde.PDL;
import static no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map;

@Component
@Path("/oppgave")
@Produces("application/json")
public class OppgaveResource implements OppgaveApi {

    private final OppgaveService oppgaveService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
    }

    @POST
    @Override
    public OppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        final Bruker bruker = userService.hentBrukerFra(PDL);

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        OppgaveResponse oppgaveResponse = oppgaveService.opprettOppgave(bruker, oppgaveDto.getOppgaveType());

        return map(oppgaveResponse, oppgaveDto.getOppgaveType());
    }
}
