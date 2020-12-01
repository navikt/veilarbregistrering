package no.nav.fo.veilarbregistrering.registrering.tilstand.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandService;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class InternalRegistreringStatusoversiktServlet extends HttpServlet {

    private final RegistreringTilstandService registreringTilstandService;

    public InternalRegistreringStatusoversiktServlet(RegistreringTilstandService registreringTilstandService) {
        this.registreringTilstandService = registreringTilstandService;
    }

    public static final String PATH = "/internal/statusoversikt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Status status = Status.parse(req.getParameter("status"));

        String registreringTilstandIderJson = of(registreringTilstandService.finnRegistreringTilstandMed(status).stream()
                .map(RegistreringTilstand::getId)
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
