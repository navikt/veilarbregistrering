package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.SITUASJONSBESTEMT_INNSATS;
import static no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.STANDARD_INNSATS;
import static no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StartRegistreringUtilsTest {

    private StartRegistreringUtils startRegistreringUtils = new StartRegistreringUtils();

    @Test
    void testProfilering() {
        LocalDate dagensDato = now();

        List<Integer> aldre = Arrays.asList(25, 40, 65);
        List<Boolean> tilfredsstillerKravTilArbeidList = Arrays.asList(true, false);
        List<Besvarelse> alleMuligeBesvarelser = genererAlleMuligeBesvarelser();

        alleMuligeBesvarelser.forEach(besvarelse -> {
            if (besvarelse.getUtdanning() == null) {
                System.out.println(besvarelse);
                throw new RuntimeException();
            }
            tilfredsstillerKravTilArbeidList.forEach(tilfredsstillerKrav -> {
                aldre.forEach(alder -> {
                    validerProfilering(
                            new OrdinaerBrukerRegistrering().setBesvarelse(besvarelse),
                            alder,
                            () -> getArbeidsforholdList(tilfredsstillerKrav),
                            dagensDato
                    );
                });
            });
        });
    }

    private List<Besvarelse> genererAlleMuligeBesvarelser() {
        List<Besvarelse> besvarelser = new ArrayList<>();
        Arrays.asList(DinSituasjonSvar.values()).forEach(dinSituasjonSvar -> {
            Arrays.asList(SisteStillingSvar.values()).forEach(sisteStillingSvar -> {
                Arrays.asList(UtdanningSvar.values()).forEach(utdanningSvar -> {
                    Arrays.asList(UtdanningGodkjentSvar.values()).forEach(utdanningGodkjentSvar -> {
                        Arrays.asList(UtdanningBestattSvar.values()).forEach(utdanningBestattSvar -> {
                            Arrays.asList(HelseHinderSvar.values()).forEach(helseHinderSvar -> {
                                Arrays.asList(AndreForholdSvar.values()).forEach(andreForholdSvar -> {
                                    besvarelser.add(new Besvarelse()
                                            .setDinSituasjon(dinSituasjonSvar)
                                            .setSisteStilling(sisteStillingSvar)
                                            .setUtdanning(utdanningSvar)
                                            .setUtdanningGodkjent(utdanningGodkjentSvar)
                                            .setUtdanningBestatt(utdanningBestattSvar)
                                            .setHelseHinder(helseHinderSvar)
                                            .setAndreForhold(andreForholdSvar)
                                    );
                                });
                            });
                        });
                    });
                });
            });
        });
        return besvarelser;
    }

    private void validerProfilering(
            OrdinaerBrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        Innsatsgruppe innsatsgruppe = startRegistreringUtils.profilerBruker(
                bruker,
                alder,
                arbeidsforholdSupplier,
                dagensDato
        ).getInnsatsgruppe();
        Besvarelse besvarelse = bruker.getBesvarelse();

        Innsatsgruppe onsketInnsatsgruppe;
        if (besvarelse.getHelseHinder().equals(HelseHinderSvar.JA) || besvarelse.getAndreForhold().equals(AndreForholdSvar.JA)) {
            onsketInnsatsgruppe = Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
        } else if ((18 <= alder && alder <= 59)
                    && startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato)
                    && !besvarelse.getUtdanning().equals(UtdanningSvar.INGEN_UTDANNING)
                    && besvarelse.getUtdanningBestatt().equals(UtdanningBestattSvar.JA)
                    && besvarelse.getUtdanningGodkjent().equals(UtdanningGodkjentSvar.JA)
                    && besvarelse.getHelseHinder().equals(HelseHinderSvar.NEI)
                    && besvarelse.getAndreForhold().equals(AndreForholdSvar.NEI)
        ) {
            onsketInnsatsgruppe = STANDARD_INNSATS;
        } else {
            onsketInnsatsgruppe = SITUASJONSBESTEMT_INNSATS;
        }

        assertEquals(onsketInnsatsgruppe, innsatsgruppe, "Feil profilering for bruker: " + bruker.toString());
    }

    @Test
    void testIKVALBesvarelseMellom30Og59Aar() {
        Innsatsgruppe innsatsgruppe = new StartRegistreringUtils().profilerBruker(
                hentStandardInnsatsBesvarelse(),
                35,
                () -> getArbeidsforholdList(true),
                now()
        ).getInnsatsgruppe();
        assertEquals(STANDARD_INNSATS, innsatsgruppe);
    }

    @Test
    void testBFORMBesvarelseOver59Aar() {
        Innsatsgruppe innsatsgruppe = new StartRegistreringUtils().profilerBruker(
                hentStandardInnsatsBesvarelse(),
                60,
                () -> getArbeidsforholdList(true),
                now()
        ).getInnsatsgruppe();
        assertEquals(SITUASJONSBESTEMT_INNSATS, innsatsgruppe);
    }

    @Test
    void testBKARTBesvarelse() {
        Innsatsgruppe innsatsgruppe = new StartRegistreringUtils().profilerBruker(
                hentArbeidsEvneVurderingBesvarelse(),
                40,
                () -> getArbeidsforholdList(true),
                now()
        ).getInnsatsgruppe();
        assertEquals(BEHOV_FOR_ARBEIDSEVNEVURDERING, innsatsgruppe);
    }

    private OrdinaerBrukerRegistrering hentStandardInnsatsBesvarelse() {
        return new OrdinaerBrukerRegistrering()
                .setBesvarelse(new Besvarelse()
                        .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
                        .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
                        .setUtdanningBestatt(UtdanningBestattSvar.JA)
                        .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
                        .setHelseHinder(HelseHinderSvar.NEI)
                        .setAndreForhold(AndreForholdSvar.NEI)
                );
    }

    private OrdinaerBrukerRegistrering hentArbeidsEvneVurderingBesvarelse(){
        OrdinaerBrukerRegistrering besvarelse = hentStandardInnsatsBesvarelse();
        besvarelse.getBesvarelse().setHelseHinder(HelseHinderSvar.JA);
        return besvarelse;
    }

    private List<Arbeidsforhold> getArbeidsforholdList(boolean tilfredsstillerKrav) {
        int antallManeder = tilfredsstillerKrav ? 10 : 2;
        return Collections.singletonList(
                new Arbeidsforhold()
                        .setFom(now().minusMonths(antallManeder))
                        .setTom(now())
        );

    }
}