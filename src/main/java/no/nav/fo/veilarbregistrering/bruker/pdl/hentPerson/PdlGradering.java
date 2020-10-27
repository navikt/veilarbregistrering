package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

public enum PdlGradering {
    STRENGT_FORTROLIG_UTLAND(3), // Tilsvarer paragraf 19 i Bisys (henvisning til Forvaltningslovens §19)
    STRENGT_FORTROLIG(2), // Tidligere spesregkode kode 6 fra TPS
    FORTROLIG(1), // Tidligere spesregkode kode 7 fra TPS

    UGRADERT(0);

    private final int niva;

    PdlGradering(int niva) {
        this.niva = niva;
    }

    protected int getNiva() {
        return niva;
    }
}
