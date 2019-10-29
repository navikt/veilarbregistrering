package no.nav.fo.veilarbregistrering.bruker;

import java.util.Objects;

/**
 * Geografisk tilknytning kan være 1 av 3:
 * <ul>
 *     <li>Landkode (3 bokstaver)</li>
 *     <li>Fylke (4 siffer)</li>
 *     <li>Bydel (6 siffer)</li>
 * </ul>
 */
public class GeografiskTilknytning {

    private final String geografisktilknytning;

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
