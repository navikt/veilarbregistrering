package no.nav.fo.veilarbregistrering.oppfolging;

import java.util.Objects;

public class Servicegruppe {

    private final String servicegruppe;

    public static Servicegruppe of(String servicegruppe) {
        return new Servicegruppe(servicegruppe);
    }

    private Servicegruppe(String servicegruppe) {
        if (servicegruppe == null) {
            throw new IllegalArgumentException("Servicegruppe skal ikke kunne være null. " +
                    "Hvis null, kan NullableServicegruppe brukes i stedet.");
        }
        this.servicegruppe = servicegruppe;
    }

    public String stringValue() {
        return servicegruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servicegruppe that = (Servicegruppe) o;
        return Objects.equals(servicegruppe, that.servicegruppe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicegruppe);
    }


}
