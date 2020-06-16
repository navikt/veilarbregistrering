package no.nav.fo.veilarbregistrering.registrering.resources;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering;

@Data
@Accessors(chain = true)
public class BrukerRegistreringWrapper {

    private final BrukerRegistreringType type;
    private final BrukerRegistrering registrering;

    public BrukerRegistreringWrapper(BrukerRegistrering registrering){
        this.registrering = registrering;
        type = registrering.hentType();
    }

}
