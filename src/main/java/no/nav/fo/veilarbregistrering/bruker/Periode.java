package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.util.Objects;

public class Periode {

    private final LocalDate fra;
    private final LocalDate til;

    public static Periode of(LocalDate fra, LocalDate til) {
        return new Periode(fra, til);
    }

    public static Periode gyldigPeriode(LocalDate fraOgMed, LocalDate tilOgMed) {
        if (fraOgMed == null) {
            throw new IllegalArgumentException("FraOgMed-dato er null");
        }
        if (tilOgMed != null && fraOgMed.isAfter(tilOgMed)) {
            throw new IllegalArgumentException("FraOgMed-dato er etter TilOgMed-dato");
        }
        return new Periode(fraOgMed, tilOgMed);
    }

    private Periode(LocalDate fra, LocalDate til) {
        this.fra = fra;
        this.til = til;
    }

    /**
     * Er periode er Åpen, dersom "til"-dato er null.
     */
    public boolean erApen() {
        return til == null;
    }

    public String fraDatoAs_yyyyMMdd() {
        return fra.toString();
    }

    public String tilDatoAs_yyyyMMdd() {
        return til != null ? til.toString() : null;
    }

    public LocalDate getFra() {
        return fra;
    }

    public LocalDate getTil() {
        return til;
    }

    public boolean overlapperMed(Periode forespurtPeriode) {
        if (forespurtPeriodeAvsluttesFørPeriodeStarter(forespurtPeriode)) {
            return false;
        }

        if (forespurtPeriodeStarterEtterPeriodeErAvsluttet(forespurtPeriode)) {
            return false;
        }

        return true;
    }

    private boolean forespurtPeriodeStarterEtterPeriodeErAvsluttet(Periode forespurtPeriode) {
        return til != null && forespurtPeriode.getFra().isAfter(til);
    }

    private boolean forespurtPeriodeAvsluttesFørPeriodeStarter(Periode forespurtPeriode) {
        return forespurtPeriode.getTil() != null && fra.isAfter(forespurtPeriode.getTil());
    }

    public boolean fraOgMed(Periode periode) {
        return fra.equals(periode.getFra()) || fra.isAfter(periode.getFra());
    }

    @Override
    public String toString() {
        return "{" +
                "fra=" + fra +
                ", til=" + til +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Periode periode = (Periode) o;
        return Objects.equals(fra, periode.fra) &&
                Objects.equals(til, periode.til);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fra, til);
    }
}
