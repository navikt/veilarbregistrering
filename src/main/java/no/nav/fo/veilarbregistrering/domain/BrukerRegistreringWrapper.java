package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;

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
