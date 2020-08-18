package no.nav.fo.veilarbregistrering.bruker;

import java.util.List;
import java.util.Objects;

public class Bruker {

    private final Foedselsnummer gjeldendeFoedselsnummer;
    private final AktorId aktorId;
    private List<Foedselsnummer> historiskeFoedselsnummer;

    public static Bruker of(Foedselsnummer foedselsnummer, AktorId aktorId) {
        return new Bruker(foedselsnummer, aktorId);
    }

    public static Bruker of(
            Foedselsnummer gjeldendeFoedselsnummer,
            AktorId gjeldendeAktorId,
            List<Foedselsnummer> historiskeFoedselsnummer) {
        return new Bruker(gjeldendeFoedselsnummer, gjeldendeAktorId, historiskeFoedselsnummer);
    }

    private Bruker(Foedselsnummer gjeldendeFoedselsnummer, AktorId aktorId) {
        this.gjeldendeFoedselsnummer = gjeldendeFoedselsnummer;
        this.aktorId = aktorId;
    }

    private Bruker(
            Foedselsnummer gjeldendeFoedselsnummer,
            AktorId aktorId,
            List<Foedselsnummer> historiskeFoedselsnummer) {
        this.gjeldendeFoedselsnummer = gjeldendeFoedselsnummer;
        this.aktorId = aktorId;
        this.historiskeFoedselsnummer = historiskeFoedselsnummer;
    }

    public Foedselsnummer getGjeldendeFoedselsnummer() {
        return gjeldendeFoedselsnummer;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public List<Foedselsnummer> getHistoriskeFoedselsnummer() {
        return historiskeFoedselsnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bruker)) return false;
        Bruker bruker = (Bruker) o;
        return getGjeldendeFoedselsnummer().equals(bruker.getGjeldendeFoedselsnummer()) &&
                getAktorId().equals(bruker.getAktorId()) &&
                getHistoriskeFoedselsnummer().equals(bruker.getHistoriskeFoedselsnummer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGjeldendeFoedselsnummer(), getAktorId(), getHistoriskeFoedselsnummer());
    }
}
