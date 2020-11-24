package no.nav.fo.veilarbregistrering.sykemelding.adapter;

public class InfotrygdData {
    public String maksDato;

    public InfotrygdData(String maksDato) {
        this.maksDato = maksDato;
    }

    public InfotrygdData() {
    }

    public String getMaksDato() {
        return this.maksDato;
    }

    public void setMaksDato(String maksDato) {
        this.maksDato = maksDato;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof InfotrygdData)) return false;
        final InfotrygdData other = (InfotrygdData) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$maksDato = this.getMaksDato();
        final Object other$maksDato = other.getMaksDato();
        if (this$maksDato == null ? other$maksDato != null : !this$maksDato.equals(other$maksDato)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof InfotrygdData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $maksDato = this.getMaksDato();
        result = result * PRIME + ($maksDato == null ? 43 : $maksDato.hashCode());
        return result;
    }

    public InfotrygdData withMaksDato(String maksDato) {
        return this.maksDato == maksDato ? this : new InfotrygdData(maksDato);
    }

    public String toString() {
        return "InfotrygdData(maksDato=" + this.getMaksDato() + ")";
    }
}