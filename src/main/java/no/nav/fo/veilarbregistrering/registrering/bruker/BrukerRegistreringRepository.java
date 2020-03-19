package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface BrukerRegistreringRepository {

    OrdinaerBrukerRegistrering lagreOrdinaerBruker(OrdinaerBrukerRegistrering bruker, AktorId aktorId);

    long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId);

    OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId);

    OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId);

    SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId);

    void lagreReaktiveringForBruker(AktorId aktorId);

    long lagre(RegistreringTilstand registreringTilstand);

    RegistreringTilstand hentRegistreringTilstand(long id);
}
