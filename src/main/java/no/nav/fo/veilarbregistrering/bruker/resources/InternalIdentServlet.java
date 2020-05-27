package no.nav.fo.veilarbregistrering.bruker.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class InternalIdentServlet extends HttpServlet {

    private UserService userService;

    public InternalIdentServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        FnrDto fnrDto = mapper.readValue(req.getInputStream(), FnrDto.class);
        Foedselsnummer foedselsnummer = Foedselsnummer.of(fnrDto.getFnr());
        Bruker bruker = userService.hentBruker(foedselsnummer);
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
