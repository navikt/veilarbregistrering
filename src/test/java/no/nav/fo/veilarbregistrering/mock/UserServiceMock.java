package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.service.UserService;

public class UserServiceMock extends UserService {

    public String getFnr() {
        return "10108000398"; //Aremark fiktivt fnr.
    }
}
