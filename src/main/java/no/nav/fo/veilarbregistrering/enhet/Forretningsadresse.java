package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.bruker.Periode;

public class Forretningsadresse implements Adresse {

    private final Kommune kommune;
    private final Periode periode;

    public Forretningsadresse(Kommune kommune, Periode periode) {
        this.kommune = kommune;
        this.periode = periode;
    }

    @Override
    public Kommune getKommunenummer() {
        return kommune;
    }

    @Override
    public boolean erGyldig() {
        return periode.erApen();
    }
}
