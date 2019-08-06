package no.nav.fo.veilarbregistrering.profilering;

import java.util.Arrays;
import java.util.Optional;

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

    public static Innsatsgruppe tilInnsatsgruppe(String arenakode) {
        Optional<Innsatsgruppe> innsatsgruppeOptional = Arrays.stream(Innsatsgruppe.values())
                .filter(innsatsgruppe -> innsatsgruppe.getArenakode().equals(arenakode))
                .findAny();
        return innsatsgruppeOptional.orElse(null);
    }
}
