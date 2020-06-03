package no.nav.fo.veilarbregistrering.registrering.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.bruker.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class InternalRegistreringTilstandServlet extends HttpServlet {

    private final BrukerRegistreringService brukerRegistreringService;

    public InternalRegistreringTilstandServlet(BrukerRegistreringService brukerRegistreringService) {
        this.brukerRegistreringService = brukerRegistreringService;
    }

    public static final String PATH = "/internal/tilstand";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Status status = Status.parse(req.getParameter("status"));

        String registreringIderJson = of(brukerRegistreringService.finnRegistreringTilstandMed(status).stream()
                .map(RegistreringTilstand::getBrukerRegistreringId).collect(toList()))
                .map(registreringer -> new Gson().toJson(registreringer)).get();

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        out.print(registreringIderJson);
        out.flush();
        out.close();
    }
}
