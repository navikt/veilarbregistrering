package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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

        pepClient.sjekkSkrivetilgangTilBruker(bruker);

        Oppgave oppgave = oppgaveService.opprettOppgave(
                AktorId.valueOf(bruker.getAktoerId()),
                Foedselsnummer.of(bruker.getFoedselsnummer()));

        LOG.info("Oppgave {} ble opprettet og tildelt {}", oppgave.getId(), oppgave.getTildeltEnhetsnr());

        return map(oppgave);
    }

}
