package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.util.Objects;

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

    public Formidlingsgruppe getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidssokerperiode that = (Arbeidssokerperiode) o;
        return Objects.equals(formidlingsgruppe, that.formidlingsgruppe) &&
                Objects.equals(periode, that.periode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formidlingsgruppe, periode);
    }

    @Override
    public String toString() {
        return "Arbeidssokerperiode{" +
                "formidlingsgruppe=" + formidlingsgruppe +
                ", periode=" + periode +
                '}';
    }
}
