package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.common.abac.Pep;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map;


@RestController
@RequestMapping("/api/oppgave")
public class OppgaveResource implements OppgaveApi {

    private final OppgaveService oppgaveService;
    private Pep pepClient;
    private final UserService userService;

    public OppgaveResource(
            Pep pepClient,
            UserService userService,
            OppgaveService oppgaveService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.oppgaveService = oppgaveService;
    }

    @Override
    @PostMapping
    public OppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        // TODO pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        OppgaveResponse oppgaveResponse = oppgaveService.opprettOppgave(bruker, oppgaveDto.getOppgaveType());

        return map(oppgaveResponse, oppgaveDto.getOppgaveType());
    }
}
