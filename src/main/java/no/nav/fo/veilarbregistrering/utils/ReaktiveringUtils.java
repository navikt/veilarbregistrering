package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;

import java.time.LocalDate;
import java.time.Period;

public class ReaktiveringUtils {
    public static boolean kreverReaktivering(AktivStatus aktivStatus) {
        return !aktivStatus.isAktiv() && harVartRegistrertSiste28dager(aktivStatus.getInaktiveringDato());
    }

    static boolean harVartRegistrertSiste28dager(LocalDate inaktiveringsDato) {
        if (inaktiveringsDato == null) {
            return false;
        }
        Period interval = Period.between(inaktiveringsDato, LocalDate.now());
        return interval.getMonths() == 0 && interval.getDays() <= 28;
    }
}
