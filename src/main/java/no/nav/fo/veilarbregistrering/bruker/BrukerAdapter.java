package no.nav.fo.veilarbregistrering.bruker;

public class BrukerAdapter {

    public static no.nav.apiapp.security.veilarbabac.Bruker map(Bruker bruker) {
        return no.nav.apiapp.security.veilarbabac.Bruker
                .fraFnr(bruker.getGjeldendeFoedselsnummer().stringValue())
                .medAktoerId(bruker.getAktorId().asString());
    }
}
