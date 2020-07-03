package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;

public interface BrukerRegistreringRepository {

    OrdinaerBrukerRegistrering lagre(OrdinaerBrukerRegistrering registrering, Bruker bruker);

    long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId);

    OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId);

    OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId);

    SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId);

    void lagreReaktiveringForBruker(AktorId aktorId);

    Bruker hentBrukerTilknyttet(long brukerRegistreringId);
}
