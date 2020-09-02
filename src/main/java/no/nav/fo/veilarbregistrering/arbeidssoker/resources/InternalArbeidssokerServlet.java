package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.bruker.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class InternalArbeidssokerServlet extends HttpServlet {

    public static final String PATH = "/internal/arbeidssoker";

    private UserService userService;
    private ArbeidssokerService arbeidssokerService;

    public InternalArbeidssokerServlet(UserService userService, ArbeidssokerService arbeidssokerService) {
        this.userService = userService;
        this.arbeidssokerService = arbeidssokerService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate fraOgMed = LocalDate.parse(req.getParameter("fraOgMed"));
        LocalDate tilOgMed = ofNullable(req.getParameter("tilOgMed")).map(LocalDate::parse).orElse(null);

        Bruker bruker = ofNullable(req.getParameter("fnr"))
                .map(Foedselsnummer::of)
                .map(userService::finnBrukerGjennomPdl)
                .orElseThrow(() -> new BadRequestException("Fnr eller aktørid må spesifiseres"));

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(
                bruker, Periode.gyldigPeriode(fraOgMed, tilOgMed));

        ArbeidssokerperioderDto dto = map(arbeidssokerperiodes.eldsteFoerst());

        String json = new Gson().toJson(dto);

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        out.print(json);
        out.flush();
        out.close();
    }

    private ArbeidssokerperioderDto map(List<Arbeidssokerperiode> arbeidssokerperioder) {
        List<ArbeidssokerperiodeDto> arbeidssokerperiodeDtoer = arbeidssokerperioder.stream()
                .map(periode -> new ArbeidssokerperiodeDto(
                        periode.getPeriode().getFra().toString(),
                        ofNullable(periode.getPeriode().getTil())
                                .map(LocalDate::toString)
                                .orElse(null)))
                .collect(Collectors.toList());

        return new ArbeidssokerperioderDto(arbeidssokerperiodeDtoer);
    }
}
