package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.util.Objects;

public class Foedselsnummer {

    private final String foedselsnummer;

    public static Foedselsnummer of(String foedselsnummer) {
        return new Foedselsnummer(foedselsnummer);
    }

    private Foedselsnummer(String foedselsnummer) {
        Objects.requireNonNull(foedselsnummer, "Foedselsnummer kan ikke være null.");
        this.foedselsnummer = foedselsnummer;
    }

    public String stringValue() {
        return foedselsnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foedselsnummer that = (Foedselsnummer) o;
        return Objects.equals(foedselsnummer, that.foedselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foedselsnummer);
    }

    public int alder(LocalDate dato) {
        return FnrUtils.utledAlderForFnr(foedselsnummer, dato);
    }
}
