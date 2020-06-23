package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.util.Objects;

public class Periode {

    private final LocalDate fra;
    private final LocalDate til;

    public static Periode of(LocalDate fra, LocalDate til) {
        return new Periode(fra, til);
    }

    private Periode(LocalDate fra, LocalDate til) {
        this.fra = fra;
        this.til = til;
    }

    @Override
    public String toString() {
        return "Periode{" +
                "fra=" + fra +
                ", til=" + til +
                '}';
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
