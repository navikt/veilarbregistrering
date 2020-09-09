package no.nav.fo.veilarbregistrering.bruker;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private final Provider<HttpServletRequest> requestProvider;
    private final PdlOppslagGateway pdlOppslagGateway;

    public UserService(Provider<HttpServletRequest> requestProvider, PdlOppslagGateway pdlOppslagGateway) {
        this.requestProvider = requestProvider;
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    public Bruker finnBrukerGjennomPdl() {
        Foedselsnummer fnr = hentFnrFraUrlEllerToken();
        return finnBrukerGjennomPdl(fnr);
    }

    public Bruker finnBrukerGjennomPdl(Foedselsnummer fnr) {
        return map(pdlOppslagGateway.hentIdenter(fnr));
    }

    public Bruker hentBruker(AktorId aktorId) {
        return map(pdlOppslagGateway.hentIdenter(aktorId));
    }

    private static Bruker map(Identer identer) {
        return Bruker.of(
                identer.finnGjeldendeFnr(),
                identer.finnGjeldendeAktorId(),
                identer.finnHistoriskeFoedselsnummer());
    }

    private Foedselsnummer hentFnrFraUrlEllerToken() {

        String fnr = getFnrFromUrl();

        if (fnr == null) {
            fnr = getFnr();
        }

        if (!isValid(fnr)) {
            throw new RuntimeException("Fødselsnummer ikke gyldig.");
        }

        return Foedselsnummer.of(fnr);
    }

    public String getFnrFromUrl() {
        return requestProvider.get().getParameter("fnr");
    }

    private String getFnr() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

    public String getEnhetIdFromUrlOrThrow() {
        final String enhetId = requestProvider.get().getParameter("enhetId");

        if (enhetId == null) {
            throw new RuntimeException("Mangler enhetId");
        }

        return enhetId;
    }
}
