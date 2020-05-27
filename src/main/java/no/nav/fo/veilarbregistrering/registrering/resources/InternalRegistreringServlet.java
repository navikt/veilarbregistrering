package no.nav.fo.veilarbregistrering.registrering.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InternalRegistreringServlet extends HttpServlet {

    private final BrukerRegistreringService brukerRegistreringService;
    private final UnleashService unleashService;

    public InternalRegistreringServlet(BrukerRegistreringService brukerRegistreringService, UnleashService unleashService) {
        this.brukerRegistreringService = brukerRegistreringService;
        this.unleashService = unleashService;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        RegistreringTilstandDto registreringTilstand = mapper.readValue(req.getInputStream(), RegistreringTilstandDto.class);

        if (internalOppgaveApiErAktivt()) {
            // TODO: Returnere objektet?
            brukerRegistreringService.oppdaterRegistreringTilstand(registreringTilstand);
        }
    }

    private boolean internalOppgaveApiErAktivt() {
        return unleashService.isEnabled("veilarbregistrering.api.internal");
    }

}
