package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.util.Objects;

public class TildeltEnhetsnr implements Metric {

    private final String tildeltEnhetsnr;

    private TildeltEnhetsnr(String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    static TildeltEnhetsnr of(String tildeltEnhetsnr) {
        return new TildeltEnhetsnr(tildeltEnhetsnr);
    }

    @Override
    public String fieldName() {
        return "tildeltEnhetsnr";
    }

    @Override
    public String value() {
        return tildeltEnhetsnr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TildeltEnhetsnr that = (TildeltEnhetsnr) o;
        return Objects.equals(tildeltEnhetsnr, that.tildeltEnhetsnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tildeltEnhetsnr);
    }
}
