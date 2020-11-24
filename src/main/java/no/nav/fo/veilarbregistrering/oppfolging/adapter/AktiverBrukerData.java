package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;

public class AktiverBrukerData {
    Fnr fnr;
    Innsatsgruppe innsatsgruppe;

    public AktiverBrukerData(Fnr fnr, Innsatsgruppe innsatsgruppe) {
        this.fnr = fnr;
        this.innsatsgruppe = innsatsgruppe;
    }

    public AktiverBrukerData() {
    }

    public Fnr getFnr() {
        return this.fnr;
    }

    public Innsatsgruppe getInnsatsgruppe() {
        return this.innsatsgruppe;
    }

    public void setFnr(Fnr fnr) {
        this.fnr = fnr;
    }

    public void setInnsatsgruppe(Innsatsgruppe innsatsgruppe) {
        this.innsatsgruppe = innsatsgruppe;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AktiverBrukerData)) return false;
        final AktiverBrukerData other = (AktiverBrukerData) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$fnr = this.getFnr();
        final Object other$fnr = other.getFnr();
        if (this$fnr == null ? other$fnr != null : !this$fnr.equals(other$fnr)) return false;
        final Object this$innsatsgruppe = this.getInnsatsgruppe();
        final Object other$innsatsgruppe = other.getInnsatsgruppe();
        if (this$innsatsgruppe == null ? other$innsatsgruppe != null : !this$innsatsgruppe.equals(other$innsatsgruppe))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AktiverBrukerData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $fnr = this.getFnr();
        result = result * PRIME + ($fnr == null ? 43 : $fnr.hashCode());
        final Object $innsatsgruppe = this.getInnsatsgruppe();
        result = result * PRIME + ($innsatsgruppe == null ? 43 : $innsatsgruppe.hashCode());
        return result;
    }

    public String toString() {
        return "AktiverBrukerData(fnr=" + this.getFnr() + ", innsatsgruppe=" + this.getInnsatsgruppe() + ")";
    }
}
