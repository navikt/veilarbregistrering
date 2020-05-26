package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;

class FristFerdigstillelse {

    private final LocalDate fristFerdigstillelse;

    static FristFerdigstillelse fristFerdigstillelse(LocalDate dagensDato) {
        LocalDate localDate = dagensDato;
        int tellerDager = 0;
        int antallDager = 2;
        while (tellerDager < antallDager) {
            localDate = localDate.plusDays(1);
            if (Ukedag.erHelg(localDate)) {
                continue;
            }

            if (Helligdager.erHelligdag(localDate)) {
                continue;
            }

            tellerDager++;
        }

        return new FristFerdigstillelse(localDate);
    }

    private FristFerdigstillelse(LocalDate fristFerdigstillelse) {
        this.fristFerdigstillelse = fristFerdigstillelse;
    }

    public LocalDate asLocalDate() {
        return fristFerdigstillelse;
    }
}
