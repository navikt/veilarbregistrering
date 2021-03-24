package no.nav.fo.veilarbregistrering.registrering.formidling.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidling;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingService;
import no.nav.fo.veilarbregistrering.registrering.formidling.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class InternalRegistreringStatusoversiktServlet extends HttpServlet {

    private final RegistreringFormidlingService registreringFormidlingService;

    public InternalRegistreringStatusoversiktServlet(RegistreringFormidlingService registreringFormidlingService) {
        this.registreringFormidlingService = registreringFormidlingService;
    }

    public static final String PATH = "/internal/statusoversikt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Status status = Status.parse(req.getParameter("status"));

        String registreringTilstandIderJson = of(registreringFormidlingService.finnRegistreringTilstandMed(status).stream()
                .map(RegistreringFormidling::getId)
                .collect(toList()))
                .map(registreringTilstander -> new Gson().toJson(registreringTilstander)).get();

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        out.print(registreringTilstandIderJson);
        out.flush();
        out.close();
    }
}
