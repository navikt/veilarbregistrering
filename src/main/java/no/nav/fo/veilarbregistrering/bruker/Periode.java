package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;

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
}
