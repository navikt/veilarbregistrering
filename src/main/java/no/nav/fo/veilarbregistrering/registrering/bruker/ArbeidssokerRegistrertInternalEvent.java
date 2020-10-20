package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ArbeidssokerRegistrertInternalEvent {

    private final AktorId aktorId;
    private final OrdinaerBrukerRegistrering brukerRegistrering;

    public ArbeidssokerRegistrertInternalEvent(AktorId aktorId, OrdinaerBrukerRegistrering brukerRegistrering) {
        this.aktorId = aktorId;
        this.brukerRegistrering = brukerRegistrering;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public Optional<DinSituasjonSvar> getBrukersSituasjon() {
        return ofNullable(brukerRegistrering.getBrukersSituasjon());
    }

    public Optional<UtdanningSvar> getUtdanningSvar() {
        return ofNullable(brukerRegistrering.getUtdanningSvar());
    }

    public Optional<UtdanningBestattSvar> getUtdanningBestattSvar() {
        return ofNullable(brukerRegistrering.getUtdanningBestattSvar());
    }

    public Optional<UtdanningGodkjentSvar> getUtdanningGodkjentSvar() {
        return ofNullable(brukerRegistrering.getUtdanningGodkjentSvar());
    }

    public LocalDateTime getOpprettetDato() {
        return brukerRegistrering.getOpprettetDato();
    }
}
