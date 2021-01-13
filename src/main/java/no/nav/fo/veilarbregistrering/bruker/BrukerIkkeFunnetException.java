package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.feil.Feil;
import no.nav.fo.veilarbregistrering.feil.FeilType;

public class BrukerIkkeFunnetException extends Feil {
    public BrukerIkkeFunnetException(String melding) {
        super(FeilType.FINNES_IKKE, melding);
    }
}
