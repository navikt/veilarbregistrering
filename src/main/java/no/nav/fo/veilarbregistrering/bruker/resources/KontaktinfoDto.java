package no.nav.fo.veilarbregistrering.bruker.resources;


public class KontaktinfoDto {

    private String telefonnummerHosKrr;
    private String telefonnummerHosNav;

    public KontaktinfoDto() {
    }

    public String getTelefonnummerHosKrr() {
        return this.telefonnummerHosKrr;
    }

    public String getTelefonnummerHosNav() {
        return this.telefonnummerHosNav;
    }

    public KontaktinfoDto setTelefonnummerHosKrr(String telefonnummerHosKrr) {
        this.telefonnummerHosKrr = telefonnummerHosKrr;
        return this;
    }

    public KontaktinfoDto setTelefonnummerHosNav(String telefonnummerHosNav) {
        this.telefonnummerHosNav = telefonnummerHosNav;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof KontaktinfoDto)) return false;
        final KontaktinfoDto other = (KontaktinfoDto) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$telefonnummerHosKrr = this.getTelefonnummerHosKrr();
        final Object other$telefonnummerHosKrr = other.getTelefonnummerHosKrr();
        if (this$telefonnummerHosKrr == null ? other$telefonnummerHosKrr != null : !this$telefonnummerHosKrr.equals(other$telefonnummerHosKrr))
            return false;
        final Object this$telefonnummerHosNav = this.getTelefonnummerHosNav();
        final Object other$telefonnummerHosNav = other.getTelefonnummerHosNav();
        if (this$telefonnummerHosNav == null ? other$telefonnummerHosNav != null : !this$telefonnummerHosNav.equals(other$telefonnummerHosNav))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof KontaktinfoDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $telefonnummerHosKrr = this.getTelefonnummerHosKrr();
        result = result * PRIME + ($telefonnummerHosKrr == null ? 43 : $telefonnummerHosKrr.hashCode());
        final Object $telefonnummerHosNav = this.getTelefonnummerHosNav();
        result = result * PRIME + ($telefonnummerHosNav == null ? 43 : $telefonnummerHosNav.hashCode());
        return result;
    }

    public String toString() {
        return "KontaktinfoDto(telefonnummerHosKrr=" + this.getTelefonnummerHosKrr() + ", telefonnummerHosNav=" + this.getTelefonnummerHosNav() + ")";
    }
}
