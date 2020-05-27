package no.nav.fo.veilarbregistrering.bruker;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private final Provider<HttpServletRequest> requestProvider;
    private final AktorGateway aktorGateway;

    public UserService(Provider<HttpServletRequest> requestProvider, AktorGateway aktorGateway) {
        this.requestProvider = requestProvider;
        this.aktorGateway = aktorGateway;
    }

    public Bruker hentBruker() {
        return hentBruker(hentFnrFraUrlEllerToken());
    }

    public Bruker hentBruker(Foedselsnummer fnr) {
        AktorId aktorId = aktorGateway.hentAktorIdFor(fnr);

        return Bruker.of(fnr, aktorId);
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

    public String getFnr() {
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
