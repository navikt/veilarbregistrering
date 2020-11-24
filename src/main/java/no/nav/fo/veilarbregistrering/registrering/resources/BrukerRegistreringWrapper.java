package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering;

public class BrukerRegistreringWrapper {

    private final BrukerRegistreringType type;
    private final BrukerRegistrering registrering;

    public BrukerRegistreringWrapper(BrukerRegistrering registrering){
        this.registrering = registrering;
        type = registrering.hentType();
    }

    public BrukerRegistreringType getType() {
        return this.type;
    }

    public BrukerRegistrering getRegistrering() {
        return this.registrering;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BrukerRegistreringWrapper))
            return false;
        final BrukerRegistreringWrapper other = (BrukerRegistreringWrapper) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$registrering = this.getRegistrering();
        final Object other$registrering = other.getRegistrering();
        if (this$registrering == null ? other$registrering != null : !this$registrering.equals(other$registrering))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BrukerRegistreringWrapper;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $registrering = this.getRegistrering();
        result = result * PRIME + ($registrering == null ? 43 : $registrering.hashCode());
        return result;
    }

    public String toString() {
        return "BrukerRegistreringWrapper(type=" + this.getType() + ", registrering=" + this.getRegistrering() + ")";
    }
}
