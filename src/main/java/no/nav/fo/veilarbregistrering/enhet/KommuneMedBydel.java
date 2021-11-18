package no.nav.fo.veilarbregistrering.enhet;

import java.util.Arrays;

public enum KommuneMedBydel {

    OSLO("0301"),
    BERGEN("4601"),
    STAVANGER("1103"),
    TRONDHEIM("5001");

    final String kommenummer;

    KommuneMedBydel(String kommenummer) {
        this.kommenummer = kommenummer;
    }

    static boolean contains(String kommenummer) {
        return Arrays.stream(KommuneMedBydel.values())
                .anyMatch(k -> k.kommenummer.equals(kommenummer));
    }
}
