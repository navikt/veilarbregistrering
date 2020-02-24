package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Maksdato angir det tidspunktet hvor bruker når maks antall dager på sykepenger.
 */
public class Maksdato implements Metric {

    private static final int MAKS_ANTALL_UKER_MED_SYKEPENGER = 52;

    private final String maksdato;

    public static Maksdato of(String maksdato) {
        return new Maksdato(maksdato);
    }

    private Maksdato(String maksdato) {
        Objects.requireNonNull(maksdato, "Maksdato kan ikke være null");
        this.maksdato = maksdato;
    }

    public static Maksdato nullable() {
        return new Maksdato.NullableMaksdato();
    }

    public boolean beregnSykmeldtMellom39Og52Uker(LocalDate dagenDato) {
        LocalDate dato = LocalDate.parse(maksdato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }

    public String asString() {
        return maksdato;
    }

    @Override
    public String toString() {
        return "Maksdato{ " +
                maksdato + " (maksdato) - " +
                LocalDate.now() + " (dagens dato) = " +
                this.antallUkerSykmeldt(LocalDate.now()) + " (uker sykmeldt) " +
                '}';
    }

    /**
     * Regner ut antall uker en person har hatt sykepenger, gitt en dato og maksdato som er hentet fra Infotrygd.
     */
    long antallUkerSykmeldt(LocalDate dato) {
        LocalDate _maksdato = LocalDate.parse(maksdato);
        return MAKS_ANTALL_UKER_MED_SYKEPENGER - ChronoUnit.WEEKS.between(dato, _maksdato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maksdato maksdato1 = (Maksdato) o;
        return Objects.equals(maksdato, maksdato1.maksdato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maksdato);
    }

    @Override
    public String fieldName() {
        return "maksdato";
    }

    @Override
    public String value() {
        return Long.toString(antallUkerSykmeldt(LocalDate.now()));
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableMaksdato extends Maksdato {

        private NullableMaksdato() {
            super("INGEN_VERDI");
        }

        @Override
        public boolean beregnSykmeldtMellom39Og52Uker(LocalDate dagenDato) {
            return false;
        }

        @Override
        public String asString() {
            return null;
        }

        @Override
        public String value() {
            return super.asString();
        }

        @Override
        public String toString() {
            return "Maksdato{ " +
                    null + " (maksdato) - " +
                    LocalDate.now() + " (dagens dato) = " +
                    "ukjent (uker sykmeldt) " +
                    '}';
        }
    }
}
