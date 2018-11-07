package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.TekstForSporsmal;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static no.bekk.bekkopen.person.FodselsnummerCalculator.getFodselsnummerForDate;

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

    public static List<TekstForSporsmal> gyldigeTeksterForBesvarelse() {
        List<TekstForSporsmal> tekster = new ArrayList<>();
        tekster.add(new TekstForSporsmal("utdanning", "Hva er din høyeste fullførte utdanning?", "Høyere utdanning (5 år eller mer)"));
        tekster.add(new TekstForSporsmal("utdanningBestatt", "Er utdanningen din bestått?", "Ja"));
        tekster.add(new TekstForSporsmal("utdanningGodkjent", "Er utdanningen din godkjent i Norge?", "Nei"));
        tekster.add(new TekstForSporsmal("helseHinder", "Trenger du oppfølging i forbindelse med helseutfordringer?", "Nei"));
        tekster.add(new TekstForSporsmal("andreForhold", "Trenger du oppfølging i forbindelse med andre utfordringer?", "Nei"));
        tekster.add(new TekstForSporsmal("sisteStilling", "Din siste jobb", "Har hatt jobb"));
        tekster.add(new TekstForSporsmal("dinSituasjon", "Hvorfor registrerer du deg?", "Jeg er permittert eller vil bli permittert"));
        return tekster;
    }

    public static List<TekstForSporsmal> gyldigeTeksterForSykmeldtBesvarelse() {
        List<TekstForSporsmal> tekster = new ArrayList<>();
        tekster.add(new TekstForSporsmal("fremtidigSituasjon", "Hva tenker du om din fremtidige situasjon?", "Jeg skal tilbake til jobben jeg har"));
        tekster.add(new TekstForSporsmal("tilbakeIArbeid", "Tror du at du kommer tilbake i jobb før du har vært sykmeldt i 52 uker?", "Nei"));
        return tekster;
    }

    public static Stilling gyldigStilling() {
        return new Stilling()
                .setStyrk08("12345")
                .setLabel("yrkesbeskrivelse")
                .setKonseptId(1246345L);
    }

    public static Stilling ingenYrkesbakgrunn() {
        return new Stilling("X", -1L, "X");
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

    public static Besvarelse gyldigSykmeldtBesvarelse() {
        return new Besvarelse()
                .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
                .setTilbakeEtter52uker(TilbakeEtter52ukerSvar.JA_FULL_STILLING);
    }

    public static Besvarelse gyldigBesvarelseUtenJobb() {
        return gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR);
    }

    public static OrdinaerBrukerRegistrering gyldigBrukerRegistrering() {
        return new OrdinaerBrukerRegistrering()
                .setOpprettetDato(LocalDateTime.now())
                .setEnigIOppsummering(true)
                .setOppsummering("Test test oppsummering")
                .setSisteStilling(gyldigStilling())
                .setBesvarelse(gyldigBesvarelse())
                .setTeksterForBesvarelse(gyldigeTeksterForBesvarelse());

    }

    public static OrdinaerBrukerRegistrering gyldigSykmeldtRegistrering() {
        return new OrdinaerBrukerRegistrering()
                .setOpprettetDato(LocalDateTime.now())
                .setBesvarelse(gyldigSykmeldtBesvarelse())
                .setTeksterForBesvarelse(gyldigeTeksterForSykmeldtBesvarelse());
    }

    public static OrdinaerBrukerRegistrering gyldigBrukerRegistreringUtenJobb() {
        return gyldigBrukerRegistrering().setSisteStilling(
                ingenYrkesbakgrunn()
        ).setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
    }
}
