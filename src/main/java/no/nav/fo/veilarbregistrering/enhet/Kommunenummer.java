package no.nav.fo.veilarbregistrering.enhet;

import java.util.Objects;

public class Kommunenummer {

    private final String kommunenummer;

    public static Kommunenummer of(String kommunenummer) {
        return new Kommunenummer(kommunenummer);
    }

    public static Kommunenummer of(KommuneMedBydel kommuneMedBydel) {
        return new Kommunenummer(kommuneMedBydel.kommenummer);
    }

    private Kommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

    public String asString() {
        return kommunenummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kommunenummer that = (Kommunenummer) o;
        return Objects.equals(kommunenummer, that.kommunenummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kommunenummer);
    }

    public boolean kommuneMedBydeler() {
        return KommuneMedBydel.contains(kommunenummer);
    }

}
