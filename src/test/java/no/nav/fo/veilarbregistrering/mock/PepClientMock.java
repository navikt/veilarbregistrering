package no.nav.fo.veilarbregistrering.mock;

import no.nav.apiapp.feil.IngenTilgang;
import no.nav.apiapp.security.PepClient;
import no.nav.sbl.dialogarena.common.abac.pep.exception.PepException;

public class PepClientMock extends PepClient {

    public PepClientMock() {
      super(null, null, null);
    }

    public String sjekkLeseTilgangTilFnr(String fnr) {
        return "";
    }

    public String sjekkSkriveTilgangTilFnr(String fnr) {
        return "";
    }

    public void sjekkTilgangTilEnhet(String enhet) throws IngenTilgang, PepException {
    }

    public boolean harTilgangTilEnhet(String enhet) throws PepException {
        return true;
    }
}
