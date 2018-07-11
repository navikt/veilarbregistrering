package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;

import java.time.LocalDate;
import java.time.Period;

public class ReaktiveringUtils {
    public static boolean kreverReaktivering(AktivStatus aktivStatus) {
        return !aktivStatus.isAktiv() && harVaertRegistrertILopetAvDeSiste28Dagene(aktivStatus.getInaktiveringDato());
    }

    static boolean harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate inaktiveringsDato) {
        if (inaktiveringsDato == null) {
            return false;
        }
        Period inaktiveringsIntervall = Period.between(inaktiveringsDato, LocalDate.now());
        return inaktiveringsIntervall.getYears() == 0
                && inaktiveringsIntervall.getMonths() == 0
                && inaktiveringsIntervall.getDays() <= 28;
    }
}
