package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.util.List;

public class Arbeidssokerperiode {

    private final Formidlingsgruppe formidlingsgruppe;
    private final Periode periode;

    public Arbeidssokerperiode(Formidlingsgruppe formidlingsgruppe, Periode periode) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.periode = periode;
    }

    public Periode getPeriode() {
        return periode;
    }
}
