package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlStatsborgerskap {
    private String land;
    private String gyldigFraOgMed;
    private String gyldigTilOgMed;

    public PdlStatsborgerskap() {

    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getGyldigFraOgMed() {
        return gyldigFraOgMed;
    }

    public void setGyldigFraOgMed(String gyldigFraOgMed) {
        this.gyldigFraOgMed = gyldigFraOgMed;
    }

    public String getGyldigTilOgMed() {
        return gyldigTilOgMed;
    }

    public void setGyldigTilOgMed(String gyldigTilOgMed) {
        this.gyldigTilOgMed = gyldigTilOgMed;
    }
}
