package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

public interface ManuellRegistreringRepository {

    long lagreManuellRegistrering(ManuellRegistrering manuellRegistrering);

    ManuellRegistrering hentManuellRegistrering(long registreringId, BrukerRegistreringType brukerRegistreringType);
}
