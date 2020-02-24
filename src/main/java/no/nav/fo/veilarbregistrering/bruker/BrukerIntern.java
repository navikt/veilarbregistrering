package no.nav.fo.veilarbregistrering.bruker;

public class BrukerIntern {

    private final Foedselsnummer foedselsnummer;
    private final AktorId aktorId;

    public static BrukerIntern of(Foedselsnummer foedselsnummer, AktorId aktorId) {
        return new BrukerIntern(foedselsnummer, aktorId);
    }

    private BrukerIntern(Foedselsnummer foedselsnummer, AktorId aktorId) {
        this.foedselsnummer = foedselsnummer;
        this.aktorId = aktorId;
    }

    public Foedselsnummer getFoedselsnummer() {
        return foedselsnummer;
    }

    public AktorId getAktorId() {
        return aktorId;
    }
}
