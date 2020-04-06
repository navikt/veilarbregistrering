package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface DatakvalitetOppholdstillatelseService {
    void hentOgSammenlignOppholdFor(AktorId aktorid);
}
