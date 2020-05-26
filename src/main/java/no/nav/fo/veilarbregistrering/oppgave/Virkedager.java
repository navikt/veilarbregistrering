package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;

class Virkedager {

    private Virkedager() {
    }

    static LocalDate plussAntallArbeidsdager(LocalDate dato, int antallDager) {
        int teller = 0;
        int tellerDager = 0;

        while (teller != antallDager) {
            tellerDager++;
            if (Ukedag.erHelg(dato.plusDays(tellerDager))) {
                continue;
            }

            if (Helligdager.erHelligdag(dato.plusDays(tellerDager))) {
                continue;
            }

            teller++;
        }

        return dato.plusDays(tellerDager);
    }
}
