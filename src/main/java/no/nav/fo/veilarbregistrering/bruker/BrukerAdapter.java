package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.security.veilarbabac.Bruker;

public class BrukerAdapter {

    public static Bruker map(BrukerIntern bruker) {
        return Bruker
                .fraFnr(bruker.getFoedselsnummer().stringValue())
                .medAktoerId(bruker.getAktorId().asString());
    }
}
