package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.service.UserService;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

public class UserServiceMock extends UserService {

    public UserServiceMock(Provider<HttpServletRequest> requestProvider) {
        super(requestProvider);
    }

    public String getFnr() {
        return "10108000398"; //Aremark fiktivt fnr.
    }
}
