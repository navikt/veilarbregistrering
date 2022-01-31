package no.nav.fo.veilarbregistrering.orgenhet;

public class NavEnhet {
    String id;
    String navn;

    public NavEnhet(String id, String navn) {
        this.id = id;
        this.navn = navn;
    }

    public NavEnhet() {
    }

    public String getId() {
        return this.id;
    }

    public String getNavn() {
        return this.navn;
    }

    public NavEnhet setId(String id) {
        this.id = id;
        return this;
    }

    public NavEnhet setNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NavEnhet)) return false;
        final NavEnhet other = (NavEnhet) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$navn = this.getNavn();
        final Object other$navn = other.getNavn();
        if (this$navn == null ? other$navn != null : !this$navn.equals(other$navn)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NavEnhet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $navn = this.getNavn();
        result = result * PRIME + ($navn == null ? 43 : $navn.hashCode());
        return result;
    }

    public String toString() {
        return "NavEnhet(id=" + this.getId() + ", navn=" + this.getNavn() + ")";
    }
}
