package no.nav.fo.veilarbregistrering.bruker;

import java.util.Arrays;
import java.util.Objects;

/**
 * Geografisk tilknytning kan være 1 av 3:
 * <ul>
 *     <li>Landkode (3 bokstaver)</li>
 *     <li>Fylke (4 siffer)</li>
 *     <li>Bydel (6 siffer)</li>
 * </ul>
 */
public class GeografiskTilknytning implements Metrikkel {

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

    @Override
    public String fiedldName() {
        String fieldName = null;

        if (utland()) {
            fieldName = "utland";
        } else if (fylke()) {
            fieldName = "fylke";
        } else if (bydelIkkeOslo()) {
            fieldName = "bydel.ikke.oslo";
        } else if (bydelOslo()) {
            fieldName = "bydel.oslo." + BydelOslo.of(geografisktilknytning).kode;
        } else {
            throw new IllegalArgumentException("Geografisk tilknytning har ukjent format: " + geografisktilknytning);
        }

        return fieldName;
    }

    private boolean utland() {
        return geografisktilknytning.length() == 3 && geografisktilknytning.matches("^[a-åA-Å]*$");
    }

    private boolean fylke() {
        return geografisktilknytning.length() == 4 && geografisktilknytning.matches("^[0-9]*$");
    }

    private boolean bydelOslo() {
        return geografisktilknytning.length() == 6 && BydelOslo.contains(geografisktilknytning);
    }

    private boolean bydelIkkeOslo() {
        return geografisktilknytning.length() == 6 && !BydelOslo.contains(geografisktilknytning);
    }

    @Override
    public int value() {
        return 1;
    }

    private enum BydelOslo {

        GamleOslo("030101", "Gamle Oslo"),
        Grunerlokka("030102", "Grünerløkka"),
        Sagene("030103", "Sagene"),
        StHanshaugen("030104", "St.Hanshaugen"),
        Frogner("030105", "Frogner"),
        Ullern("030106", "Ullern"),
        VestreAker("030107", "Vestre Aker"),
        NordreAker("030108", "Nordre Aker"),
        Bjerke("030109", "Bjerke"),
        Grorud("030110", "Grorud"),
        Stovner("030111", "Stovner"),
        Alna("030112", "Alna"),
        Ostensjo("030113", "Østensjø"),
        Nordstrand("030114", "Nordstrand"),
        SondreNordstrand("030115", "Søndre Nordstrand"),
        Sentrum("030116", "Sentrum"),
        Marka("030117", "Marka");

        private final String kode;
        private final String verdi;

        BydelOslo(String kode, String verdi) {
            this.kode = kode;
            this.verdi = verdi;
        };

        private static BydelOslo of(String geografisktilknytning) {
            return Arrays.stream(BydelOslo.values())
                    .filter(bydelOslo -> bydelOslo.kode.equals(geografisktilknytning))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(""));
        }

        private static boolean contains(String geografisktilknytning) {
            return Arrays.stream(BydelOslo.values())
                    .filter(bydelOslo -> bydelOslo.kode.equals(geografisktilknytning))
                    .findAny()
                    .isPresent();
        }
    }

}
