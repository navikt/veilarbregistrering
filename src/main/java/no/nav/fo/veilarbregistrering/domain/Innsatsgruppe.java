package no.nav.fo.veilarbregistrering.domain;

public enum Innsatsgruppe {
    STANDARD_INNSATS("IKVAL"),
    SITUASJONSBESTEMT_INNSATS("BFORM"),
    BEHOV_FOR_ARBEIDSEVNEVURDERING("BKART");

    private String arenakode;

    Innsatsgruppe(String arenakode) {
        this.arenakode = arenakode;
    }

    public String getArenakode() {
        return arenakode;
    }
}
