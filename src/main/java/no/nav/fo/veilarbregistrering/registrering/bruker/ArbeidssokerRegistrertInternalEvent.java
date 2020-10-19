package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

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

    public DinSituasjonSvar getBrukersSituasjon() {
        return brukerRegistrering.getBrukersSituasjon();
    }

    public LocalDateTime getOpprettetDato() {
        return brukerRegistrering.getOpprettetDato();
    }
}
