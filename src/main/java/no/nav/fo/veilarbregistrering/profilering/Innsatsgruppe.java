package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

import java.util.Arrays;

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

    public static Innsatsgruppe of(String arenakode) {
        return Arrays.stream(Innsatsgruppe.values())
                .filter(innsatsgruppe -> innsatsgruppe.getArenakode().equals(arenakode))
                .findAny().orElse(null);
    }

    static Innsatsgruppe of(Besvarelse besvarelse, int alder, boolean harJobbetSammenhengendeSeksAvTolvSisteManeder) {
        if (besvarelse.anbefalerBehovForArbeidsevnevurdering()) {
            return Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
        } else if (besvarelse.anbefalerStandardInnsats(alder, harJobbetSammenhengendeSeksAvTolvSisteManeder)) {
            return Innsatsgruppe.STANDARD_INNSATS;
        } else {
            return Innsatsgruppe.SITUASJONSBESTEMT_INNSATS;
        }
    }

}
