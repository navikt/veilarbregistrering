package no.nav.fo.veilarbregistrering.arbeidsforhold;

/**
 * Nisifret nummer som entydig identifiserer enheter i Enhetsregisteret.
 */
public class Organisasjonsnummer {

    private final String organisasjonsnummer;

    public static Organisasjonsnummer of(String organisasjonsnummer) {
        return new Organisasjonsnummer(organisasjonsnummer);
    }

    private Organisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }
}
