package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

import java.time.LocalDate;

public class PdlStatsborgerskap {
    private String land;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;

    public PdlStatsborgerskap() {

    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public LocalDate getGyldigFraOgMed() {
        return gyldigFraOgMed;
    }

    public void setGyldigFraOgMed(LocalDate gyldigFraOgMed) {
        this.gyldigFraOgMed = gyldigFraOgMed;
    }

    public LocalDate getGyldigTilOgMed() {
        return gyldigTilOgMed;
    }

    public void setGyldigTilOgMed(LocalDate gyldigTilOgMed) {
        this.gyldigTilOgMed = gyldigTilOgMed;
    }
}
