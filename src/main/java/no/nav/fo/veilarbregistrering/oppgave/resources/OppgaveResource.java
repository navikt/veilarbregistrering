package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;
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

    public OppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Oppretter oppgave 'kontakt bruker'")
    public OppgaveDto opprettOppgave() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        Oppgave oppgave = oppgaveService.opprettOppgave(bruker);

        LOG.info("Oppgave {} ble opprettet og tildelt {}", oppgave.getId(), oppgave.getTildeltEnhetsnr());

        return map(oppgave);
    }

    @POST
    @Path("/oppholdstillatelse")
    @ApiOperation(value = "Oppretter oppgave 'kontakt bruker' - arbeidssøkerregistrering der NAV ikke vet om brukeren har oppholdstillatelse")
    public OppgaveDto opprettOppgaveArbeidstillatelse() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        Oppgave oppgave = oppgaveService.opprettOppgaveArbeidstillatelse(bruker, OppgaveType.OPPHOLDSTILLATELSE);

        LOG.info("Oppgave {} ble opprettet", oppgave.getId());

        return map(oppgave);
    }
}
