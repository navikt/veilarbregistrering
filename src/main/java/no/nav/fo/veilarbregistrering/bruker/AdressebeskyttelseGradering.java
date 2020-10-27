package no.nav.fo.veilarbregistrering.bruker;

public enum AdressebeskyttelseGradering {
    STRENGT_FORTROLIG_UTLAND, // Tilsvarer paragraf 19 i Bisys (henvisning til Forvaltningslovens §19)
    STRENGT_FORTROLIG, // Tidligere spesregkode kode 6 fra TPS
    FORTROLIG, // Tidligere spesregkode kode 7 fra TPS

    UGRADERT, // Kode vi kan få fra Folkeregisteret, men brukscaset er ukjent.

    UKJENT;

    public boolean erGradert() {
        return this != UGRADERT && this != UKJENT;
    }
}
