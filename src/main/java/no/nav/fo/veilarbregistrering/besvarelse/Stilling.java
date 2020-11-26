package no.nav.fo.veilarbregistrering.besvarelse;

public class Stilling {
    private String label;
    private long konseptId;
    private String styrk08;

    public Stilling(String label, long konseptId, String styrk08) {
        this.label = label;
        this.konseptId = konseptId;
        this.styrk08 = styrk08;
    }

    public Stilling() {
    }

    public static Stilling tomStilling() {
        return new Stilling("", -1L, "-1");
    }

    public static Stilling ingenYrkesbakgrunn() {
        return new Stilling("X", -1L, "X");
    }

    public String getLabel() {
        return this.label;
    }

    public long getKonseptId() {
        return this.konseptId;
    }

    public String getStyrk08() {
        return this.styrk08;
    }

    public Stilling setLabel(String label) {
        this.label = label;
        return this;
    }

    public Stilling setKonseptId(long konseptId) {
        this.konseptId = konseptId;
        return this;
    }

    public Stilling setStyrk08(String styrk08) {
        this.styrk08 = styrk08;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Stilling)) return false;
        final Stilling other = (Stilling) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$label = this.getLabel();
        final Object other$label = other.getLabel();
        if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
        if (this.getKonseptId() != other.getKonseptId()) return false;
        final Object this$styrk08 = this.getStyrk08();
        final Object other$styrk08 = other.getStyrk08();
        if (this$styrk08 == null ? other$styrk08 != null : !this$styrk08.equals(other$styrk08)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Stilling;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $label = this.getLabel();
        result = result * PRIME + ($label == null ? 43 : $label.hashCode());
        final long $konseptId = this.getKonseptId();
        result = result * PRIME + (int) ($konseptId >>> 32 ^ $konseptId);
        final Object $styrk08 = this.getStyrk08();
        result = result * PRIME + ($styrk08 == null ? 43 : $styrk08.hashCode());
        return result;
    }

    public String toString() {
        return "Stilling(label=" + this.getLabel() + ", konseptId=" + this.getKonseptId() + ", styrk08=" + this.getStyrk08() + ")";
    }
}
