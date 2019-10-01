package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;

public class ArbeidsforholdUtils {

    static int antallMnd = 12;
    static int minAntallMndSammenhengendeJobb = 6;
    static int dagIMnd = 1;

    /**
     * En bruker som har jobbet sammenhengende i seks av de siste tolv månedene oppfyller betingelsen om arbeidserfaring
     */
    public static boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(FlereArbeidsforhold flereArbeidsforhold, LocalDate dagensDato) {
        int antallSammenhengendeMandeder = 0;
        int mndFraDagensMnd = 0;
        LocalDate innevaerendeMnd = LocalDate.of(dagensDato.getYear(), dagensDato.getMonthValue(), dagIMnd);

        while (antallSammenhengendeMandeder < minAntallMndSammenhengendeJobb && mndFraDagensMnd < antallMnd) {

            if (flereArbeidsforhold.harArbeidsforholdPaaDato(innevaerendeMnd)) {
                antallSammenhengendeMandeder += 1;
            } else {
                antallSammenhengendeMandeder = 0;
            }

            innevaerendeMnd = innevaerendeMnd.minusMonths(1);
            mndFraDagensMnd += 1;
        }
        return antallSammenhengendeMandeder >= minAntallMndSammenhengendeJobb;
    }

}
