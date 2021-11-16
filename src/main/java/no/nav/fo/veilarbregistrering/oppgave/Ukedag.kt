package no.nav.fo.veilarbregistrering.oppgave;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

class Ukedag {

    private static final List<DayOfWeek> HELG = Arrays.asList(SATURDAY, SUNDAY);

    static boolean erHelg(LocalDate dato) {
        return HELG.contains(dato.getDayOfWeek());
    }
}
