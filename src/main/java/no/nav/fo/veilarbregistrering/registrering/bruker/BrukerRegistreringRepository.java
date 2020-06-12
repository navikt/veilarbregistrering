package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrukerRegistreringRepository {

    OrdinaerBrukerRegistrering lagre(OrdinaerBrukerRegistrering registrering, Bruker bruker);

    long lagreSykmeldtBruker(SykmeldtRegistrering bruker, AktorId aktorId);

    OrdinaerBrukerRegistrering hentBrukerregistreringForId(long brukerregistreringId);

    OrdinaerBrukerRegistrering hentOrdinaerBrukerregistreringForAktorId(AktorId aktorId);

    SykmeldtRegistrering hentSykmeldtregistreringForAktorId(AktorId aktorId);

    void lagreReaktiveringForBruker(AktorId aktorId);

    long lagre(AktiveringTilstand registreringTilstand);

    AktiveringTilstand hentAktiveringTilstand(long id);

    Optional<AktiveringTilstand> finnNesteAktiveringTilstandForOverforing();

    void oppdater(AktiveringTilstand aktiveringTilstand);

    Bruker hentBrukerTilknyttet(long brukerRegistreringId);

    Page<ArbeidssokerRegistrertEventDto> findRegistreringByPage(Pageable pageable);

    List<AktiveringTilstand> finnAktiveringTilstandMed(Status status);

    Optional<AktiveringTilstand> finnNesteAktiveringTilstandSomHarFeilet();
}
