package no.nav.fo.veilarbregistrering.enhet;

import java.util.Arrays;
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

    public enum KommuneMedBydel {

        OSLO("0301"),
        BERGEN("4601"),
        STAVANGER("1103"),
        TRONDHEIM("5001");

        private final String kommenummer;

        KommuneMedBydel(String kommenummer) {
            this.kommenummer = kommenummer;
        }

        private static boolean contains(String kommenummer) {
            return Arrays.stream(KommuneMedBydel.values())
                    .anyMatch(k -> k.kommenummer.equals(kommenummer));
        }
    }
}
