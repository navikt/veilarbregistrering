package no.nav.fo.veilarbregistrering.bruker;

public class FoedselsnummerTestdataBuilder {

    /**
     * Returnerer fødselsnummer til Aremark som er fiktivt
     */
    public static Foedselsnummer aremark() {
        return Foedselsnummer.of("10108000398");
    }
}
