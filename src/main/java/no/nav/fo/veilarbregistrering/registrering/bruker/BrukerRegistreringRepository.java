package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;

import java.util.Optional;

public interface BrukerRegistreringRepository {

    OrdinaerBrukerRegistrering lagreOrdinaerBruker(OrdinaerBrukerRegistrering registrering, Bruker bruker);

    OrdinaerBrukerRegistrering lagreOrdinaerBrukerMedFnr(OrdinaerBrukerRegistrering registrering, Bruker bruker);

    long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId);

    OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId);

    OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId);

    SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId);

    void lagreReaktiveringForBruker(AktorId aktorId);

    long opprett(RegistreringTilstand registreringTilstand);

    RegistreringTilstand hentRegistreringTilstand(long id);

    Optional<RegistreringTilstand> finnNesteRegistreringForOverforing();

    void oppdater(RegistreringTilstand registreringTilstand1);
}
