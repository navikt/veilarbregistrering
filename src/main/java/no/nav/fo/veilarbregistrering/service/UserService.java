package no.nav.fo.veilarbregistrering.service;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private Provider<HttpServletRequest> requestProvider;

    public UserService (Provider<HttpServletRequest> requestProvider) {
        this.requestProvider = requestProvider;
    }

    public String getFnr() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

    public String getFnrFromUrl() {
        return requestProvider.get().getParameter("fnr");
    }

}
