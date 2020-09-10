package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

public class PdlGeografiskTilknytning {

    private GtType gtType;
    private String gtKommune;
    private String gtBydel;
    private String gtLand;

    public GtType getGtType() {
        return gtType;
    }

    public void setGtType(GtType gtType) {
        this.gtType = gtType;
    }

    public String getGtKommune() {
        return gtKommune;
    }

    public void setGtKommune(String gtKommune) {
        this.gtKommune = gtKommune;
    }

    public String getGtBydel() {
        return gtBydel;
    }

    public void setGtBydel(String gtBydel) {
        this.gtBydel = gtBydel;
    }

    public String getGtLand() {
        return gtLand;
    }

    public void setGtLand(String gtLand) {
        this.gtLand = gtLand;
    }

    @Override
    public String toString() {
        return "PdlGeografiskTilknytning{" +
                "gtType=" + gtType +
                ", gtKommune='" + gtKommune + '\'' +
                ", gtBydel='" + gtBydel + '\'' +
                ", gtLand='" + gtLand + '\'' +
                '}';
    }
}
