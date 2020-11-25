package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ArbeidssokerRegistrertInternalEvent {

    private final AktorId aktorId;
    private final Besvarelse besvarelse;
    private final LocalDateTime opprettetDato;

    public ArbeidssokerRegistrertInternalEvent(AktorId aktorId, Besvarelse besvarelse, LocalDateTime opprettetDato) {
        this.aktorId = aktorId;
        this.besvarelse = besvarelse;
        this.opprettetDato = opprettetDato;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public Optional<DinSituasjonSvar> getBrukersSituasjon() {
        return ofNullable(besvarelse.getDinSituasjon());
    }

    public Optional<UtdanningSvar> getUtdanningSvar() {
        return ofNullable(besvarelse.getUtdanning());
    }

    public Optional<UtdanningBestattSvar> getUtdanningBestattSvar() {
        return ofNullable(besvarelse.getUtdanningBestatt());
    }

    public Optional<UtdanningGodkjentSvar> getUtdanningGodkjentSvar() {
        return ofNullable(besvarelse.getUtdanningGodkjent());
    }

    public LocalDateTime getOpprettetDato() {
        return opprettetDato;
    }
}
