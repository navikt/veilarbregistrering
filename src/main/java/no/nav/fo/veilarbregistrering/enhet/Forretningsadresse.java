package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.function.Predicate;

public class Forretningsadresse {

    private final Kommunenummer kommunenummer;
    private final Periode periode;

    public Forretningsadresse(Kommunenummer kommunenummer, Periode periode) {
        this.kommunenummer = kommunenummer;
        this.periode = periode;
    }

    public Kommunenummer getKommunenummer() {
        return kommunenummer;
    }

    boolean erGyldig() {
        return periode.erApen();
    }
}
