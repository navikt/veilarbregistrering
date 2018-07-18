package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static no.bekk.bekkopen.person.FodselsnummerCalculator.getFodselsnummerForDate;
import static org.assertj.core.util.DateUtil.now;

public class TestUtils {

    public static String getFodselsnummerOnDateMinusYears(LocalDate localDate, int minusYears) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears).toInstant());
        return getFodselsnummerForDate(date).toString();
    }

    public static String getFodselsnummerForPersonWithAge(int age) {
        return getFodselsnummerOnDateMinusYears(LocalDate.now(), age);
    }

    public static Profilering lagProfilering() {
        return new Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setAlder(62)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(false);
    }

    public static Stilling gyldigStilling() {
        return new Stilling()
                .setStyrk08("12345")
                .setLabel("yrkesbeskrivelse")
                .setKonseptId(1246345L);
    }

    public static Besvarelse gyldigBesvarelse() {
        return new Besvarelse()
                .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
                .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
                .setUtdanningBestatt(UtdanningBestattSvar.JA)
                .setHelseHinder(HelseHinderSvar.NEI)
                .setAndreForhold(AndreForholdSvar.NEI);
    }

    public static BrukerRegistrering gyldigBrukerRegistrering() {
        return new BrukerRegistrering()
                .setOpprettetDato(now())
                .setEnigIOppsummering(true)
                .setOppsummering("Test test oppsummering")
                .setSisteStilling(gyldigStilling())
                .setBesvarelse(gyldigBesvarelse());

    }
}
