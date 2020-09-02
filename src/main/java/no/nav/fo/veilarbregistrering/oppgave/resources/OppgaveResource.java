package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;
import static no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map;

@Component
@Path("/oppgave")
@Produces("application/json")
public class OppgaveResource implements OppgaveApi {

    private final OppgaveService oppgaveService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final UnleashService unleashService;

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService,
            UnleashService unleashService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
        this.unleashService = unleashService;
    }

    @POST
    @Override
    public OppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        final Bruker bruker = userService.hentBrukerFra(velgKilde());

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        OppgaveResponse oppgaveResponse = oppgaveService.opprettOppgave(bruker, oppgaveDto.getOppgaveType());

        return map(oppgaveResponse, oppgaveDto.getOppgaveType());
    }

    private UserService.Kilde velgKilde() {
        return unleashService.isEnabled("veilarbregistrering.oppgave.kildePdl") ? UserService.Kilde.PDL : UserService.Kilde.AKTOR;
    }
}
