package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrukerRegistreringRepository {

    OrdinaerBrukerRegistrering lagre(OrdinaerBrukerRegistrering registrering, Bruker bruker);

    long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId);

    OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId);

    OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId);

    SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId);

    void lagreReaktiveringForBruker(AktorId aktorId);

    Bruker hentBrukerTilknyttet(long brukerRegistreringId);

    Page<ArbeidssokerRegistrertInternalEvent> findRegistreringByPage(Pageable pageable);
}
