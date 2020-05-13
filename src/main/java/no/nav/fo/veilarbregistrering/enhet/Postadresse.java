package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.bruker.Periode;

public class Postadresse {

    private final Kommunenummer kommunenummer;
    private final Periode periode;

    public Postadresse(Kommunenummer kommunenummer, Periode periode) {
        this.kommunenummer = kommunenummer;
        this.periode = periode;
    }

    public Kommunenummer getKommunenummer() {
        return kommunenummer;
    }

    public boolean erGyldig() {
        return periode.erApen();
    }
}
