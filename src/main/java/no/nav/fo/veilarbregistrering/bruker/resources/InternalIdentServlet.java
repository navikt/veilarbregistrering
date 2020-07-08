package no.nav.fo.veilarbregistrering.bruker.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class InternalIdentServlet extends HttpServlet {

    public static final String PATH = "/internal/bruker";

    private UserService userService;
    private UnleashService unleashService;

    public InternalIdentServlet(UserService userService, UnleashService unleashService) {
        this.userService = userService;
        this.unleashService = unleashService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<String> fnr = ofNullable(req.getParameter("fnr"));
        Optional<String> aktorid = ofNullable(req.getParameter("aktorid"));

        Bruker bruker;

        if (fnr.isPresent()) {
            if (hentIdenterFraPdl()) {
                bruker = userService.finnBrukerGjennomPdl(Foedselsnummer.of(fnr.get()));
            } else {
                bruker = userService.hentBruker(Foedselsnummer.of(fnr.get()));
            }
        } else if (aktorid.isPresent()) {
            bruker = userService.hentBruker(AktorId.of(aktorid.get()));
        } else {
            throw new BadRequestException("Fnr eller aktørid må spesifiseres");
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

    private boolean hentIdenterFraPdl() {
        return unleashService.isEnabled("veilarbregistrering.arbeidssoker.internal.identerfrapdl");
    }

}
