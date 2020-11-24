package no.nav.fo.veilarbregistrering.oppfolging.adapter;

public class Fnr {
    private String fnr;

    public Fnr(String fnr) {
        this.fnr = fnr;
    }

    public Fnr() {
    }

    public String getFnr() {
        return this.fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Fnr)) return false;
        final Fnr other = (Fnr) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$fnr = this.getFnr();
        final Object other$fnr = other.getFnr();
        if (this$fnr == null ? other$fnr != null : !this$fnr.equals(other$fnr)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Fnr;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $fnr = this.getFnr();
        result = result * PRIME + ($fnr == null ? 43 : $fnr.hashCode());
        return result;
    }

    public String toString() {
        return "Fnr(fnr=" + this.getFnr() + ")";
    }
}
