package no.nav.fo.veilarbregistrering.registrering.formidling.resources;

import no.nav.fo.veilarbregistrering.registrering.formidling.OppdaterRegistreringFormidlingCommand;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingService;
import no.nav.fo.veilarbregistrering.registrering.formidling.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InternalRegistreringStatusServlet extends HttpServlet {

    private final RegistreringFormidlingService registreringFormidlingService;

    public InternalRegistreringStatusServlet(RegistreringFormidlingService registreringFormidlingService) {
        this.registreringFormidlingService = registreringFormidlingService;
    }

    public static final String PATH = "/internal/status";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Status status = Status.parse(req.getParameter("status"));
        String id = req.getParameter("id");

        registreringFormidlingService.oppdaterRegistreringTilstand(OppdaterRegistreringFormidlingCommand.of(id, status));
    }
}
