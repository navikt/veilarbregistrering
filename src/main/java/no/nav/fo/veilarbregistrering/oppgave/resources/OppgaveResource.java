package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map;


@RestController
@RequestMapping("/api/oppgave")
public class OppgaveResource implements OppgaveApi {

    private final OppgaveService oppgaveService;
    private final UserService userService;
    private final AutorisasjonService autorisasjonService;

    public OppgaveResource(
            UserService userService,
            OppgaveService oppgaveService,
            AutorisasjonService autorisasjonService) {
        this.userService = userService;
        this.oppgaveService = oppgaveService;
        this.autorisasjonService = autorisasjonService;
    }

    @Override
    @PostMapping
    public OppgaveDto opprettOppgave(@RequestBody OppgaveDto oppgaveDto) {
        if (oppgaveDto == null || oppgaveDto.getOppgaveType() == null) {
            throw new IllegalArgumentException("Oppgave m/ type må være angitt");
        }
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonService.sjekkSkrivetilgangTilBruker(bruker.getGjeldendeFoedselsnummer());

        OppgaveResponse oppgaveResponse = oppgaveService.opprettOppgave(bruker, oppgaveDto.getOppgaveType());

        return map(oppgaveResponse, oppgaveDto.getOppgaveType());
    }
}
