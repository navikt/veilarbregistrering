package no.nav.fo.veilarbregistrering.service;

import no.nav.brukerdialog.security.domain.IdentType;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.common.auth.SubjectHandler.getIdentType;

public class UserService {

    private Provider<HttpServletRequest> requestProvider;

    public UserService (Provider<HttpServletRequest> requestProvider) {
        this.requestProvider = requestProvider;
    }

    public boolean erEksternBruker() {
        return getIdentType()
                .map(identType -> IdentType.EksternBruker == identType)
                .orElse(false);
    }

    public String getUid() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

    public String getFnrFromUrl() {
        return requestProvider.get().getParameter("fnr");
    }

    public String hentFnrFraUrlEllerToken() {

        String fnr = getFnrFromUrl();

        if (fnr == null) {
            fnr = getUid();
        }

        if (!isValid(fnr)) {
            throw new RuntimeException("Fødselsnummer ikke gyldig.");
        }

        return fnr;

    }

}
