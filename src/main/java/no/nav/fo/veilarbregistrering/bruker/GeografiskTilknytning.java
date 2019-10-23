package no.nav.fo.veilarbregistrering.bruker;

import java.util.Objects;
import java.util.Optional;

public class GeografiskTilknytning {

    private final String geografisktilknytning;

    public static Optional<GeografiskTilknytning> ofNullable(String geografisktilknytning) {
        return geografisktilknytning != null ?
                Optional.of(new GeografiskTilknytning(geografisktilknytning)) : Optional.empty();
    }

    public static GeografiskTilknytning of(String geografisktilknytning) {
        return new GeografiskTilknytning(geografisktilknytning);
    }

    private GeografiskTilknytning(String geografisktilknytning) {
        Objects.requireNonNull(geografisktilknytning,
                "Geografisk tilknytning kan ikke være null. Bruk <code>ofNullable</code> hvis du er usikker.");
        this.geografisktilknytning = geografisktilknytning;
    }

    public String stringValue() {
        return geografisktilknytning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeografiskTilknytning that = (GeografiskTilknytning) o;
        return Objects.equals(geografisktilknytning, that.geografisktilknytning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geografisktilknytning);
    }
}
