package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder;

@Data
public abstract class BrukerRegistrering {

    protected Veileder manueltRegistrertAv;

    public abstract BrukerRegistreringType hentType();

    public abstract long getId();

    public abstract DinSituasjonSvar getBrukersSituasjon();

    public abstract UtdanningSvar getUtdanningSvar();

    public abstract UtdanningBestattSvar getUtdanningBestattSvar();

    public abstract UtdanningGodkjentSvar getUtdanningGodkjentSvar();
}
