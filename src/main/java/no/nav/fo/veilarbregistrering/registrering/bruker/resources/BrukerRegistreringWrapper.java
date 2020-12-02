package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering;

public class BrukerRegistreringWrapper {

    private final BrukerRegistreringType type;
    private final BrukerRegistrering registrering;

    public BrukerRegistreringWrapper(BrukerRegistrering registrering){
        this.registrering = registrering;
        type = registrering.hentType();
    }

    public BrukerRegistreringType getType() {
        return this.type;
    }

    public BrukerRegistrering getRegistrering() {
        return this.registrering;
    }

}
