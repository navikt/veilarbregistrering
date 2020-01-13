package no.nav.fo.veilarbregistrering.oppgave;

class NavKontor {

    private final String navnPaKontor;
    private final String identTilKontaktperson;
    private final String navnPaKontaktperson;

    static NavKontor grünerlokka() {
        return new NavKontor("NAV Grünerløkka", "H134912", "Marthe Harsvik");
    }

    static NavKontor ringsaker() {
        return new NavKontor("NAV Ringsaker", "B125772", "Inger Johanne Bryn");
    }

    static NavKontor falkenborg() {
        return new NavKontor("NAV Falkenborg", "J111715", "Gunn Jørstad Skaaren");
    }

    private NavKontor(String navnPaKontor, String identTilKontaktperson, String navnPaKontaktperson) {
        this.navnPaKontor = navnPaKontor;
        this.identTilKontaktperson = identTilKontaktperson;
        this.navnPaKontaktperson = navnPaKontaktperson;
    }

    String tilordnetRessurs() {
        return identTilKontaktperson;
    }

    String beskrivelse() {
        return String.format("Denne oppgaven har bruker selv opprettet, og er en pilotering på %s." +
                " Brukeren får ikke registrert seg som arbeidssøker." +
                " Kontaktperson ved %s er %s.",
                navnPaKontor, navnPaKontor, navnPaKontaktperson);
    }

}
