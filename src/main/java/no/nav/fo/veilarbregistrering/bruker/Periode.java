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
}
