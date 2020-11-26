package no.nav.fo.veilarbregistrering.besvarelse;

public class Besvarelse {
    private UtdanningSvar utdanning;
    private UtdanningBestattSvar utdanningBestatt;
    private UtdanningGodkjentSvar utdanningGodkjent;
    private HelseHinderSvar helseHinder;
    private AndreForholdSvar andreForhold;
    private SisteStillingSvar sisteStilling;
    private DinSituasjonSvar dinSituasjon;
    private FremtidigSituasjonSvar fremtidigSituasjon;
    private TilbakeIArbeidSvar tilbakeIArbeid;

    public Besvarelse() {
    }

    public boolean anbefalerBehovForArbeidsevnevurdering() {
        return HelseHinderSvar.JA.equals(helseHinder)
                || AndreForholdSvar.JA.equals(andreForhold);
    }

    public boolean anbefalerStandardInnsats(int alder, boolean oppfyllerKravTilArbeidserfaring) {
        return (18 <= alder && alder <= 59)
                && oppfyllerKravTilArbeidserfaring
                && !UtdanningSvar.INGEN_UTDANNING.equals(utdanning)
                && UtdanningBestattSvar.JA.equals(utdanningBestatt)
                && UtdanningGodkjentSvar.JA.equals(utdanningGodkjent)
                && HelseHinderSvar.NEI.equals(helseHinder)
                && AndreForholdSvar.NEI.equals(andreForhold);
    }

    public UtdanningSvar getUtdanning() {
        return this.utdanning;
    }

    public UtdanningBestattSvar getUtdanningBestatt() {
        return this.utdanningBestatt;
    }

    public UtdanningGodkjentSvar getUtdanningGodkjent() {
        return this.utdanningGodkjent;
    }

    public HelseHinderSvar getHelseHinder() {
        return this.helseHinder;
    }

    public AndreForholdSvar getAndreForhold() {
        return this.andreForhold;
    }

    public SisteStillingSvar getSisteStilling() {
        return this.sisteStilling;
    }

    public DinSituasjonSvar getDinSituasjon() {
        return this.dinSituasjon;
    }

    public FremtidigSituasjonSvar getFremtidigSituasjon() {
        return this.fremtidigSituasjon;
    }

    public TilbakeIArbeidSvar getTilbakeIArbeid() {
        return this.tilbakeIArbeid;
    }

    public Besvarelse setUtdanning(UtdanningSvar utdanning) {
        this.utdanning = utdanning;
        return this;
    }

    public Besvarelse setUtdanningBestatt(UtdanningBestattSvar utdanningBestatt) {
        this.utdanningBestatt = utdanningBestatt;
        return this;
    }

    public Besvarelse setUtdanningGodkjent(UtdanningGodkjentSvar utdanningGodkjent) {
        this.utdanningGodkjent = utdanningGodkjent;
        return this;
    }

    public Besvarelse setHelseHinder(HelseHinderSvar helseHinder) {
        this.helseHinder = helseHinder;
        return this;
    }

    public Besvarelse setAndreForhold(AndreForholdSvar andreForhold) {
        this.andreForhold = andreForhold;
        return this;
    }

    public Besvarelse setSisteStilling(SisteStillingSvar sisteStilling) {
        this.sisteStilling = sisteStilling;
        return this;
    }

    public Besvarelse setDinSituasjon(DinSituasjonSvar dinSituasjon) {
        this.dinSituasjon = dinSituasjon;
        return this;
    }

    public Besvarelse setFremtidigSituasjon(FremtidigSituasjonSvar fremtidigSituasjon) {
        this.fremtidigSituasjon = fremtidigSituasjon;
        return this;
    }

    public Besvarelse setTilbakeIArbeid(TilbakeIArbeidSvar tilbakeIArbeid) {
        this.tilbakeIArbeid = tilbakeIArbeid;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Besvarelse)) return false;
        final Besvarelse other = (Besvarelse) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$utdanning = this.getUtdanning();
        final Object other$utdanning = other.getUtdanning();
        if (this$utdanning == null ? other$utdanning != null : !this$utdanning.equals(other$utdanning)) return false;
        final Object this$utdanningBestatt = this.getUtdanningBestatt();
        final Object other$utdanningBestatt = other.getUtdanningBestatt();
        if (this$utdanningBestatt == null ? other$utdanningBestatt != null : !this$utdanningBestatt.equals(other$utdanningBestatt))
            return false;
        final Object this$utdanningGodkjent = this.getUtdanningGodkjent();
        final Object other$utdanningGodkjent = other.getUtdanningGodkjent();
        if (this$utdanningGodkjent == null ? other$utdanningGodkjent != null : !this$utdanningGodkjent.equals(other$utdanningGodkjent))
            return false;
        final Object this$helseHinder = this.getHelseHinder();
        final Object other$helseHinder = other.getHelseHinder();
        if (this$helseHinder == null ? other$helseHinder != null : !this$helseHinder.equals(other$helseHinder))
            return false;
        final Object this$andreForhold = this.getAndreForhold();
        final Object other$andreForhold = other.getAndreForhold();
        if (this$andreForhold == null ? other$andreForhold != null : !this$andreForhold.equals(other$andreForhold))
            return false;
        final Object this$sisteStilling = this.getSisteStilling();
        final Object other$sisteStilling = other.getSisteStilling();
        if (this$sisteStilling == null ? other$sisteStilling != null : !this$sisteStilling.equals(other$sisteStilling))
            return false;
        final Object this$dinSituasjon = this.getDinSituasjon();
        final Object other$dinSituasjon = other.getDinSituasjon();
        if (this$dinSituasjon == null ? other$dinSituasjon != null : !this$dinSituasjon.equals(other$dinSituasjon))
            return false;
        final Object this$fremtidigSituasjon = this.getFremtidigSituasjon();
        final Object other$fremtidigSituasjon = other.getFremtidigSituasjon();
        if (this$fremtidigSituasjon == null ? other$fremtidigSituasjon != null : !this$fremtidigSituasjon.equals(other$fremtidigSituasjon))
            return false;
        final Object this$tilbakeIArbeid = this.getTilbakeIArbeid();
        final Object other$tilbakeIArbeid = other.getTilbakeIArbeid();
        if (this$tilbakeIArbeid == null ? other$tilbakeIArbeid != null : !this$tilbakeIArbeid.equals(other$tilbakeIArbeid))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Besvarelse;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $utdanning = this.getUtdanning();
        result = result * PRIME + ($utdanning == null ? 43 : $utdanning.hashCode());
        final Object $utdanningBestatt = this.getUtdanningBestatt();
        result = result * PRIME + ($utdanningBestatt == null ? 43 : $utdanningBestatt.hashCode());
        final Object $utdanningGodkjent = this.getUtdanningGodkjent();
        result = result * PRIME + ($utdanningGodkjent == null ? 43 : $utdanningGodkjent.hashCode());
        final Object $helseHinder = this.getHelseHinder();
        result = result * PRIME + ($helseHinder == null ? 43 : $helseHinder.hashCode());
        final Object $andreForhold = this.getAndreForhold();
        result = result * PRIME + ($andreForhold == null ? 43 : $andreForhold.hashCode());
        final Object $sisteStilling = this.getSisteStilling();
        result = result * PRIME + ($sisteStilling == null ? 43 : $sisteStilling.hashCode());
        final Object $dinSituasjon = this.getDinSituasjon();
        result = result * PRIME + ($dinSituasjon == null ? 43 : $dinSituasjon.hashCode());
        final Object $fremtidigSituasjon = this.getFremtidigSituasjon();
        result = result * PRIME + ($fremtidigSituasjon == null ? 43 : $fremtidigSituasjon.hashCode());
        final Object $tilbakeIArbeid = this.getTilbakeIArbeid();
        result = result * PRIME + ($tilbakeIArbeid == null ? 43 : $tilbakeIArbeid.hashCode());
        return result;
    }

    public String toString() {
        return "Besvarelse(utdanning=" + this.getUtdanning() + ", utdanningBestatt=" + this.getUtdanningBestatt() + ", utdanningGodkjent=" + this.getUtdanningGodkjent() + ", helseHinder=" + this.getHelseHinder() + ", andreForhold=" + this.getAndreForhold() + ", sisteStilling=" + this.getSisteStilling() + ", dinSituasjon=" + this.getDinSituasjon() + ", fremtidigSituasjon=" + this.getFremtidigSituasjon() + ", tilbakeIArbeid=" + this.getTilbakeIArbeid() + ")";
    }
}