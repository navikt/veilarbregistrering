package no.nav.fo.veilarbregistrering.bruker.krr;

public class KontaktinfoDto {

    private String epostadresse;
    /**
     * Hvorvidt brukeren ikke har reservert seg for digital post, og han/hun kan varsles digitalt
     * i henhold til eForvaltningsforskriftens §32
     */
    private boolean kanVarsles;
    private String mobiltelefonnummer;

    /**
     * Hvorvidt brukeren har reservert seg for digital post
     */
    private boolean reservert;

    KontaktinfoDto(String epostadresse, boolean kanVarsles, String mobiltelefonnummer, boolean reservert) {
        this.epostadresse = epostadresse;
        this.kanVarsles = kanVarsles;
        this.mobiltelefonnummer = mobiltelefonnummer;
        this.reservert = reservert;
    }

    public String getEpostadresse() {
        return epostadresse;
    }

    public String getMobiltelefonnummer() {
        return mobiltelefonnummer;
    }

    public boolean isKanVarsles() {
        return kanVarsles;
    }

    public boolean isReservert() {
        return reservert;
    }
}
