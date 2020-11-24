package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;

public class Veileder {
    String ident;
    NavEnhet enhet;

    public Veileder() {
    }

    public String getIdent() {
        return this.ident;
    }

    public NavEnhet getEnhet() {
        return this.enhet;
    }

    public Veileder setIdent(String ident) {
        this.ident = ident;
        return this;
    }

    public Veileder setEnhet(NavEnhet enhet) {
        this.enhet = enhet;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Veileder)) return false;
        final Veileder other = (Veileder) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$ident = this.getIdent();
        final Object other$ident = other.getIdent();
        if (this$ident == null ? other$ident != null : !this$ident.equals(other$ident)) return false;
        final Object this$enhet = this.getEnhet();
        final Object other$enhet = other.getEnhet();
        if (this$enhet == null ? other$enhet != null : !this$enhet.equals(other$enhet)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Veileder;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $ident = this.getIdent();
        result = result * PRIME + ($ident == null ? 43 : $ident.hashCode());
        final Object $enhet = this.getEnhet();
        result = result * PRIME + ($enhet == null ? 43 : $enhet.hashCode());
        return result;
    }

    public String toString() {
        return "Veileder(ident=" + this.getIdent() + ", enhet=" + this.getEnhet() + ")";
    }
}
