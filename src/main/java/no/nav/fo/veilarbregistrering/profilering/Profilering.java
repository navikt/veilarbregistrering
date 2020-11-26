package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

public class Profilering {
    boolean jobbetSammenhengendeSeksAvTolvSisteManeder;
    int alder;
    Innsatsgruppe innsatsgruppe;

    public Profilering() {
    }

    public static Profilering of(Besvarelse besvarelse, int alder, boolean harJobbetSammenhengendeSeksAvTolvSisteManeder) {
        return new Profilering()
                .setAlder(alder)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(harJobbetSammenhengendeSeksAvTolvSisteManeder)
                .setInnsatsgruppe(Innsatsgruppe.of(besvarelse, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder));
    }

    public boolean isJobbetSammenhengendeSeksAvTolvSisteManeder() {
        return this.jobbetSammenhengendeSeksAvTolvSisteManeder;
    }

    public int getAlder() {
        return this.alder;
    }

    public Innsatsgruppe getInnsatsgruppe() {
        return this.innsatsgruppe;
    }

    public Profilering setJobbetSammenhengendeSeksAvTolvSisteManeder(boolean jobbetSammenhengendeSeksAvTolvSisteManeder) {
        this.jobbetSammenhengendeSeksAvTolvSisteManeder = jobbetSammenhengendeSeksAvTolvSisteManeder;
        return this;
    }

    public Profilering setAlder(int alder) {
        this.alder = alder;
        return this;
    }

    public Profilering setInnsatsgruppe(Innsatsgruppe innsatsgruppe) {
        this.innsatsgruppe = innsatsgruppe;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Profilering)) return false;
        final Profilering other = (Profilering) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isJobbetSammenhengendeSeksAvTolvSisteManeder() != other.isJobbetSammenhengendeSeksAvTolvSisteManeder())
            return false;
        if (this.getAlder() != other.getAlder()) return false;
        final Object this$innsatsgruppe = this.getInnsatsgruppe();
        final Object other$innsatsgruppe = other.getInnsatsgruppe();
        if (this$innsatsgruppe == null ? other$innsatsgruppe != null : !this$innsatsgruppe.equals(other$innsatsgruppe))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Profilering;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isJobbetSammenhengendeSeksAvTolvSisteManeder() ? 79 : 97);
        result = result * PRIME + this.getAlder();
        final Object $innsatsgruppe = this.getInnsatsgruppe();
        result = result * PRIME + ($innsatsgruppe == null ? 43 : $innsatsgruppe.hashCode());
        return result;
    }

    public String toString() {
        return "Profilering(jobbetSammenhengendeSeksAvTolvSisteManeder=" + this.isJobbetSammenhengendeSeksAvTolvSisteManeder() + ", alder=" + this.getAlder() + ", innsatsgruppe=" + this.getInnsatsgruppe() + ")";
    }
}
