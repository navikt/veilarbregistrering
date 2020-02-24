package no.nav.fo.veilarbregistrering.bruker;


import java.util.Objects;

public class AktorId {

    private final String aktorId;

    public static AktorId valueOf(String aktorId) {
        return new AktorId(aktorId);
    }

    private AktorId(String aktorId) {
        Objects.requireNonNull(aktorId, "AktorId kan ikke være null.");
        this.aktorId = aktorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktorId aktorId1 = (AktorId) o;
        return Objects.equals(aktorId, aktorId1.aktorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktorId);
    }

    public String asString() {
        return aktorId;
    }
}
