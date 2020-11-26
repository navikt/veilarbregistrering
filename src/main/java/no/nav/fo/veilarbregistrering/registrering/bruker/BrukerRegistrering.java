package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder;

public abstract class BrukerRegistrering {

    protected Veileder manueltRegistrertAv;

    public BrukerRegistrering() {
    }

    public abstract BrukerRegistreringType hentType();

    public abstract long getId();

    public Veileder getManueltRegistrertAv() {
        return this.manueltRegistrertAv;
    }

    public void setManueltRegistrertAv(Veileder manueltRegistrertAv) {
        this.manueltRegistrertAv = manueltRegistrertAv;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BrukerRegistrering)) return false;
        final BrukerRegistrering other = (BrukerRegistrering) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$manueltRegistrertAv = this.getManueltRegistrertAv();
        final Object other$manueltRegistrertAv = other.getManueltRegistrertAv();
        if (this$manueltRegistrertAv == null ? other$manueltRegistrertAv != null : !this$manueltRegistrertAv.equals(other$manueltRegistrertAv))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BrukerRegistrering;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $manueltRegistrertAv = this.getManueltRegistrertAv();
        result = result * PRIME + ($manueltRegistrertAv == null ? 43 : $manueltRegistrertAv.hashCode());
        return result;
    }

    public String toString() {
        return "BrukerRegistrering(manueltRegistrertAv=" + this.getManueltRegistrertAv() + ")";
    }
}
