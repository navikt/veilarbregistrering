package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileringServiceTest {

    private static final Foedselsnummer FOEDSELSNUMMER_MINUS_10_MND = Foedselsnummer.of("12345678911");
    private static final Foedselsnummer FOEDSELSNUMMER_MINUS_2_MND = Foedselsnummer.of("11987654321");

    private ProfileringService profileringService;
    private ArbeidsforholdGateway arbeidsforholdGateway;

    @BeforeEach
    public void setUp() {
        arbeidsforholdGateway = new StubArbeidsforholdGateway();
        profileringService = new ProfileringService(arbeidsforholdGateway);
    }

    @Test
    void testProfilering() {
        LocalDate dagensDato = now();

        List<Integer> aldre = Arrays.asList(25, 40, 65);
        List<Foedselsnummer> tilfredsstillerKravTilArbeidList = Arrays.asList(
                FOEDSELSNUMMER_MINUS_10_MND,
                FOEDSELSNUMMER_MINUS_2_MND);
        List<Besvarelse> alleMuligeBesvarelser = genererAlleMuligeBesvarelser();

        alleMuligeBesvarelser.forEach(besvarelse -> {
            if (besvarelse.getUtdanning() == null) {
                System.out.println(besvarelse);
                throw new RuntimeException();
            }
            tilfredsstillerKravTilArbeidList.forEach(tilfredsstillerKrav -> {
                aldre.forEach(alder -> {
                    final OrdinaerBrukerRegistrering bruker = new OrdinaerBrukerRegistrering().setBesvarelse(besvarelse);
                    validerProfilering(
                            bruker,
                            alder,
                            tilfredsstillerKrav,
                            dagensDato, besvarelse
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
            Foedselsnummer foedselsnummer,
            LocalDate dagensDato, Besvarelse besvarelse
    ) {

        Innsatsgruppe innsatsgruppe = profileringService.profilerBruker(
                alder,
                foedselsnummer,
                besvarelse
        ).getInnsatsgruppe();

        Innsatsgruppe onsketInnsatsgruppe;
        if (besvarelse.getHelseHinder().equals(HelseHinderSvar.JA) || besvarelse.getAndreForhold().equals(AndreForholdSvar.JA)) {
            onsketInnsatsgruppe = Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
        } else if ((18 <= alder && alder <= 59)
                    && arbeidsforholdGateway.hentArbeidsforhold(foedselsnummer).harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)
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
        Innsatsgruppe innsatsgruppe = profileringService.profilerBruker(
                35,
                FOEDSELSNUMMER_MINUS_10_MND,
                hentStandardInnsatsBesvarelse()
        ).getInnsatsgruppe();
        assertEquals(STANDARD_INNSATS, innsatsgruppe);
    }

    @Test
    void testBFORMBesvarelseOver59Aar() {
        Innsatsgruppe innsatsgruppe = profileringService.profilerBruker(
                60,
                FOEDSELSNUMMER_MINUS_10_MND,
                hentStandardInnsatsBesvarelse()
        ).getInnsatsgruppe();
        assertEquals(SITUASJONSBESTEMT_INNSATS, innsatsgruppe);
    }

    @Test
    void testBKARTBesvarelse() {
        Innsatsgruppe innsatsgruppe = profileringService.profilerBruker(
                40,
                FOEDSELSNUMMER_MINUS_10_MND,
                hentArbeidsEvneVurderingBesvarelse()
        ).getInnsatsgruppe();
        assertEquals(BEHOV_FOR_ARBEIDSEVNEVURDERING, innsatsgruppe);
    }

    private Besvarelse hentStandardInnsatsBesvarelse() {
        return new Besvarelse()
                        .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
                        .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
                        .setUtdanningBestatt(UtdanningBestattSvar.JA)
                        .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
                        .setHelseHinder(HelseHinderSvar.NEI)
                        .setAndreForhold(AndreForholdSvar.NEI);
    }

    private Besvarelse hentArbeidsEvneVurderingBesvarelse(){
        Besvarelse besvarelse = hentStandardInnsatsBesvarelse();
        besvarelse.setHelseHinder(HelseHinderSvar.JA);
        return besvarelse;
    }

    private class StubArbeidsforholdGateway implements ArbeidsforholdGateway {

        private Map<Foedselsnummer, FlereArbeidsforhold> arbeidsforholdMap = new HashMap<>(2);

        private StubArbeidsforholdGateway() {
            arbeidsforholdMap.put(FOEDSELSNUMMER_MINUS_10_MND, FlereArbeidsforhold.of(Collections.singletonList(
                    ArbeidsforholdTestdataBuilder.medDato(now().minusMonths(10), now()))));
            arbeidsforholdMap.put(FOEDSELSNUMMER_MINUS_2_MND, FlereArbeidsforhold.of(Collections.singletonList(
                    ArbeidsforholdTestdataBuilder.medDato(now().minusMonths(2), now()))));
        }

        @Override
        public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
            return arbeidsforholdMap.get(fnr);
        }
    }
}