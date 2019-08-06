package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder;

@Data
public abstract class BrukerRegistrering {

    protected Veileder manueltRegistrertAv;

    public abstract BrukerRegistreringType hentType();

    public abstract long getId();
}
