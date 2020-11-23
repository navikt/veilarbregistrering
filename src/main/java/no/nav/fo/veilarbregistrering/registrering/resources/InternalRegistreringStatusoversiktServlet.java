package no.nav.fo.veilarbregistrering.registrering.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiveringTilstand;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiveringTilstandService;
import no.nav.fo.veilarbregistrering.registrering.bruker.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class InternalRegistreringStatusoversiktServlet extends HttpServlet {

    private final AktiveringTilstandService aktiveringTilstandService;

    public InternalRegistreringStatusoversiktServlet(AktiveringTilstandService aktiveringTilstandService) {
        this.aktiveringTilstandService = aktiveringTilstandService;
    }

    public static final String PATH = "/internal/statusoversikt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Status status = Status.parse(req.getParameter("status"));

        String aktiveringTilstandIderJson = of(aktiveringTilstandService.finnAktiveringTilstandMed(status).stream()
                .map(AktiveringTilstand::getId)
                .collect(toList()))
                .map(aktiveringTilstander -> new Gson().toJson(aktiveringTilstander)).get();

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        out.print(aktiveringTilstandIderJson);
        out.flush();
        out.close();
    }
}
