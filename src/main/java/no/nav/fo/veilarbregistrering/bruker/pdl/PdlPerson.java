package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;

public class PdlPerson {
    private List<PdlPersonOpphold> opphold;
    private List<PdlStatsborgerskap> statsborgerskap;

    public PdlPerson() {
    }

    public List<PdlPersonOpphold> getOpphold() {
        return opphold;
    }

    public void setOpphold(List<PdlPersonOpphold> opphold) {
        this.opphold = opphold;
    }

    public List<PdlStatsborgerskap> getStatsborgerskap() {
        return statsborgerskap;
    }

    public void setStatsborgerskap(List<PdlStatsborgerskap> statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }
}
