package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;

@Data
public abstract class BrukerRegistrering {

    protected Veileder manueltRegistrertAv;

    abstract BrukerRegistreringType hentType();

}
