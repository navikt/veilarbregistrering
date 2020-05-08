package no.nav.fo.veilarbregistrering.bruker.krr;

class KrrKontaktinfoDto {

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

    KrrKontaktinfoDto() {
        //default constructor
    }

    KrrKontaktinfoDto(boolean kanVarsles, String mobiltelefonnummer, boolean reservert) {
        this.kanVarsles = kanVarsles;
        this.mobiltelefonnummer = mobiltelefonnummer;
        this.reservert = reservert;
    }

    String getMobiltelefonnummer() {
        return mobiltelefonnummer;
    }

    boolean isKanVarsles() {
        return kanVarsles;
    }

    boolean isReservert() {
        return reservert;
    }
}
