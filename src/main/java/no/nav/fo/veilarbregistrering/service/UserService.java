package no.nav.fo.veilarbregistrering.service;

import no.nav.apiapp.util.SubjectUtils;

public class UserService {

    public String getFnr() {
        return SubjectUtils.getUserId().orElseThrow(IllegalArgumentException::new);
    }

}
