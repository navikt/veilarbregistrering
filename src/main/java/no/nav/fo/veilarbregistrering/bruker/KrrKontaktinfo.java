package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class KrrKontaktinfo {
    private final String mobiltelefonnummer;

    public KrrKontaktinfo(String mobiltelefonnummer) {
        this.mobiltelefonnummer = mobiltelefonnummer;
    }

    public static KrrKontaktinfo of(String mobiltelefonnummer) {
        return new KrrKontaktinfo(mobiltelefonnummer);
    }

    public Optional<String> getMobiltelefonnummer() {
        return Optional.ofNullable(this.mobiltelefonnummer);
    }
}
