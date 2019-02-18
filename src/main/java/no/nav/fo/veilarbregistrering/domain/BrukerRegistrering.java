package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;

@Data
public abstract class BrukerRegistrering {

    protected Veileder manueltRegistrertAv;

    public abstract BrukerRegistreringType hentType();

    public abstract long getId();
}
