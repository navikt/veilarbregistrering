package no.nav.fo.veilarbregistrering.bruker.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.io.PrintWriter;

public class InternalIdentServlet extends HttpServlet {

    public static final String PATH = "/internal/bruker";

    private UserService userService;

    public InternalIdentServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fnr = req.getParameter("fnr");
        String aktorid = req.getParameter("aktorid");

        if (fnr == null && aktorid == null) {
            throw new BadRequestException("Fnr eller aktørid må spesifiseres");
        }

        Bruker bruker;
        if (fnr != null) {
            bruker = userService.finnBrukerGjennomPdl(Foedselsnummer.of(fnr));
        } else {
            bruker = userService.hentBruker(AktorId.of(aktorid));
        }

        String brukerString = new Gson().toJson(bruker);

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        out.print(brukerString);
        out.flush();
        out.close();
    }
}
