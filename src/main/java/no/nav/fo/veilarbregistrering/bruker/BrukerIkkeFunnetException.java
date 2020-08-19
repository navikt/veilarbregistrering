package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;

public class BrukerIkkeFunnetException extends Feil {
    public BrukerIkkeFunnetException(String melding) {
        super(FeilType.FINNES_IKKE, melding);
    }
}
