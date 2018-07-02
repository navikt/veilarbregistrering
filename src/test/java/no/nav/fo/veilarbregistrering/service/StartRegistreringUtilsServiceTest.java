package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class StartRegistreringUtilsServiceTest {

    @Test
    void testProfilering() {
        LocalDate dagensDato = LocalDate.now();

        List<Integer> aldre = new ArrayList<>();
        aldre.add(25);
        aldre.add(40);
        aldre.add(65);

        List<Boolean> tilfredsstillerKravTilArbeid = new ArrayList<>();
        tilfredsstillerKravTilArbeid.add(true);
        tilfredsstillerKravTilArbeid.add(false);

        tilfredsstillerKravTilArbeid.forEach(tilfredsstillerKrav -> {
            aldre.forEach(alder -> {
                Arrays.asList(DinSituasjonSvar.values()).forEach(dinSituasjonSvar -> {
                    Arrays.asList(SisteStillingSvar.values()).forEach(sisteStillingSvar -> {
                        Arrays.asList(UtdanningSvar.values()).forEach(utdanningSvar -> {
                            Arrays.asList(UtdanningGodkjentSvar.values()).forEach(utdanningGodkjentSvar -> {
                                Arrays.asList(UtdanningBestattSvar.values()).forEach(utdanningBestattSvar -> {
                                    Arrays.asList(HelseHinderSvar.values()).forEach(helseHinderSvar -> {
                                        Arrays.asList(AndreForholdSvar.values()).forEach(andreForholdSvar -> {
                                            Besvarelse besvarelse = new Besvarelse()
                                                    .setDinSituasjon(dinSituasjonSvar)
                                                    .setUtdanning(utdanningSvar)
                                                    .setUtdanningGodkjent(utdanningGodkjentSvar)
                                                    .setUtdanningBestatt(utdanningBestattSvar)
                                                    .setHelseHinder(helseHinderSvar)
                                                    .setAndreForhold(andreForholdSvar);
                                            validerProfilering(
                                                    new BrukerRegistrering().setBesvarelse(besvarelse),
                                                    alder,
                                                    () -> getArbeidsforhold(tilfredsstillerKrav),
                                                    dagensDato
                                            );
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

    }

    private void validerProfilering(
            BrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        StartRegistreringUtilsService startRegistreringUtilsService = new StartRegistreringUtilsService();
        Innsatsgruppe innsatsgruppe = startRegistreringUtilsService.profilerBruker(
                bruker,
                alder,
                arbeidsforholdSupplier,
                dagensDato
        );
        Besvarelse besvarelse = bruker.getBesvarelse();

        Innsatsgruppe onsketInnsatsgruppe;

        if (besvarelse.getHelseHinder().equals(HelseHinderSvar.JA) || besvarelse.getAndreForhold().equals(AndreForholdSvar.JA)) {
            onsketInnsatsgruppe = Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
        } else if ((30 <= alder && alder <= 59)
                    && startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato)
                    && !besvarelse.getUtdanning().equals(UtdanningSvar.INGEN_UTDANNING)
                    && besvarelse.getUtdanningBestatt().equals(UtdanningBestattSvar.JA)
                    && besvarelse.getUtdanningGodkjent().equals(UtdanningGodkjentSvar.JA)
                    && besvarelse.getHelseHinder().equals(HelseHinderSvar.NEI)
                    && besvarelse.getAndreForhold().equals(AndreForholdSvar.NEI)
        ) {
            onsketInnsatsgruppe = Innsatsgruppe.STANDARD_INNSATS;
        } else {
            onsketInnsatsgruppe = Innsatsgruppe.SITUASJONSBESTEMT_INNSATS;
        }

        System.out.println(innsatsgruppe);
        System.out.println(besvarelse.toString());
        assertEquals(onsketInnsatsgruppe, innsatsgruppe, "Feil profilering for bruker: " + bruker.toString());
    }

    @Test
    void testSpesifikkBesvarelse() {
        BrukerRegistrering bruker = new BrukerRegistrering()
                .setBesvarelse(
                        new Besvarelse()
                                .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
                                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
                                .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
                                .setUtdanningBestatt(UtdanningBestattSvar.JA)
                                .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
                                .setHelseHinder(HelseHinderSvar.JA)
                                .setAndreForhold(AndreForholdSvar.NEI)
                );
        List<Arbeidsforhold> arbeidsforholdListe = getArbeidsforhold(true);
        int alder = 35;
        LocalDate dagensDato = LocalDate.now();

        StartRegistreringUtilsService startRegistreringUtilsService = new StartRegistreringUtilsService();

        Innsatsgruppe innsatsgruppe = startRegistreringUtilsService.profilerBruker(
                bruker,
                alder,
                () -> arbeidsforholdListe,
                dagensDato
        );

        assertEquals(Innsatsgruppe.STANDARD_INNSATS, innsatsgruppe);

    }

    List<Arbeidsforhold> getArbeidsforhold(boolean tilfredsstillerKrav) {
        int antallManeder = tilfredsstillerKrav ? 10 : 2;
        return Collections.singletonList(
                new Arbeidsforhold()
                        .setFom(LocalDate.now().minusMonths(antallManeder))
                        .setTom(LocalDate.now())
        );

    }
}