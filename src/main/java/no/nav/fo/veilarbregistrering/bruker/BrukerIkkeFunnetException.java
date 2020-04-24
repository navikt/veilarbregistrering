package no.nav.fo.veilarbregistrering.bruker;

public class BrukerIkkeFunnetException extends RuntimeException {
    public BrukerIkkeFunnetException(String melding) {
        super(melding);
    }
}
