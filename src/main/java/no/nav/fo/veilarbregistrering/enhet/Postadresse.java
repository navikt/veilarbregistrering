package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.bruker.Periode;

public class Postadresse implements Adresse {

    private final Kommunenummer kommunenummer;
    private final Periode periode;

    public Postadresse(Kommunenummer kommunenummer, Periode periode) {
        this.kommunenummer = kommunenummer;
        this.periode = periode;
    }

    @Override
    public Kommunenummer getKommunenummer() {
        return kommunenummer;
    }

    @Override
    public boolean erGyldig() {
        return periode.erApen();
    }
}
