package no.nav.fo.veilarbregistrering.enhet;

public class Kommunenummer {

    private final String kommunenummer;

    public static Kommunenummer of(String kommunenummer) {
        return new Kommunenummer(kommunenummer);
    }

    private Kommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

    public String asString() {
        return kommunenummer;
    }
}
