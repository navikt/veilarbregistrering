package no.nav.fo.veilarbregistrering.registrering.bruker;

public class TekstForSporsmal {
    private String sporsmalId;
    private String sporsmal;
    private String svar;

    public TekstForSporsmal(String sporsmalId, String sporsmal, String svar) {
        this.sporsmalId = sporsmalId;
        this.sporsmal = sporsmal;
        this.svar = svar;
    }

    public TekstForSporsmal() {
    }

    public String getSporsmalId() {
        return this.sporsmalId;
    }

    public String getSporsmal() {
        return this.sporsmal;
    }

    public String getSvar() {
        return this.svar;
    }

    public void setSporsmalId(String sporsmalId) {
        this.sporsmalId = sporsmalId;
    }

    public void setSporsmal(String sporsmal) {
        this.sporsmal = sporsmal;
    }

    public void setSvar(String svar) {
        this.svar = svar;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TekstForSporsmal)) return false;
        final TekstForSporsmal other = (TekstForSporsmal) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$sporsmalId = this.getSporsmalId();
        final Object other$sporsmalId = other.getSporsmalId();
        if (this$sporsmalId == null ? other$sporsmalId != null : !this$sporsmalId.equals(other$sporsmalId))
            return false;
        final Object this$sporsmal = this.getSporsmal();
        final Object other$sporsmal = other.getSporsmal();
        if (this$sporsmal == null ? other$sporsmal != null : !this$sporsmal.equals(other$sporsmal)) return false;
        final Object this$svar = this.getSvar();
        final Object other$svar = other.getSvar();
        if (this$svar == null ? other$svar != null : !this$svar.equals(other$svar)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TekstForSporsmal;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $sporsmalId = this.getSporsmalId();
        result = result * PRIME + ($sporsmalId == null ? 43 : $sporsmalId.hashCode());
        final Object $sporsmal = this.getSporsmal();
        result = result * PRIME + ($sporsmal == null ? 43 : $sporsmal.hashCode());
        final Object $svar = this.getSvar();
        result = result * PRIME + ($svar == null ? 43 : $svar.hashCode());
        return result;
    }

    public String toString() {
        return "TekstForSporsmal(sporsmalId=" + this.getSporsmalId() + ", sporsmal=" + this.getSporsmal() + ", svar=" + this.getSvar() + ")";
    }
}
