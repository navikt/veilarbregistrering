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

}
