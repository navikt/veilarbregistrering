package no.nav.fo.veilarbregistrering.oppfolging;

import java.util.Objects;

public class Rettighetsgruppe {

    private final String rettighetsgruppe;

    public static Rettighetsgruppe of(String rettighetsgruppe) {
        return new Rettighetsgruppe(rettighetsgruppe);
    }

    private Rettighetsgruppe(String rettighetsgruppe) {
        if (rettighetsgruppe == null) {
            throw new IllegalArgumentException("Rettighetsgruppe skal ikke kunne være null. " +
                    "Hvis null, kan NullableRettighetsgruppe brukes i stedet.");
        }
        this.rettighetsgruppe = rettighetsgruppe;
    }

    public String stringValue() {
        return rettighetsgruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rettighetsgruppe that = (Rettighetsgruppe) o;
        return Objects.equals(rettighetsgruppe, that.rettighetsgruppe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rettighetsgruppe);
    }
}
