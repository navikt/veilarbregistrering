package no.nav.fo.veilarbregistrering.bruker;

public class Bruker {

    private final Foedselsnummer foedselsnummer;
    private final AktorId aktorId;

    public static Bruker of(Foedselsnummer foedselsnummer, AktorId aktorId) {
        return new Bruker(foedselsnummer, aktorId);
    }

    private Bruker(Foedselsnummer foedselsnummer, AktorId aktorId) {
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
