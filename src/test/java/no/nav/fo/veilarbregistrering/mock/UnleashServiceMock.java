package no.nav.fo.veilarbregistrering.mock;

import no.nav.sbl.featuretoggle.unleash.UnleashService;

import java.util.Collections;

public class UnleashServiceMock extends UnleashService {

    public UnleashServiceMock() {
        super(null, Collections.emptyList());
    }

    @Override
    public boolean isEnabled(String a) {
        return false;
    }

}
