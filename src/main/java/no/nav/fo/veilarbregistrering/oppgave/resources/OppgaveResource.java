package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveResource.class);

    private final OppgaveService oppgaveService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final UnleashService unleashService;

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService,
            UnleashService unleashService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
        this.unleashService = unleashService;
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Oppretter oppgave 'kontakt bruker'")
    public OppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        if (skalOppretteOppgave()) {

            Oppgave oppgave = oppgaveService.opprettOppgave(bruker, oppgaveDto.oppgaveType);
            LOG.info("Oppgave ble opprettet med id: {}", oppgave.getId());
            return map(oppgave, oppgaveDto.oppgaveType);

        } else {
            return new OppgaveDto();
        }
    }

    private boolean skalOppretteOppgave() {
        return unleashService.isEnabled("veilarbregistrering.opprettOppgave");
    }
}
