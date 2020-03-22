package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;

public class PdlPerson {
    private List<PdlPersonOpphold> opphold;

    public PdlPerson() {
    }

    public List<PdlPersonOpphold> getOpphold() {
        return opphold;
    }

    public void setOpphold(List<PdlPersonOpphold> opphold) {
        this.opphold = opphold;
    }
}
