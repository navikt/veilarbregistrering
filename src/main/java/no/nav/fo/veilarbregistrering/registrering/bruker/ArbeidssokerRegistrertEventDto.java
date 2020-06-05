package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

public class ArbeidssokerRegistrertEventDto {
    private final int rowNum;
    private final long brukerRegistreringId;
    private final AktorId aktorId;
    private final String begrunnelseForRegistrering;
    private final LocalDateTime opprettetDato;

    public ArbeidssokerRegistrertEventDto(
            int rowNum,
            long brukerRegistreringId,
            AktorId aktorId,
            String begrunnelseForRegistrering,
            LocalDateTime opprettetDato) {
        this.rowNum = rowNum;
        this.brukerRegistreringId = brukerRegistreringId;
        this.aktorId = aktorId;
        this.begrunnelseForRegistrering = begrunnelseForRegistrering;
        this.opprettetDato = opprettetDato;
    }

    public Long getBrukerRegistreringId() {
        return brukerRegistreringId;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public String getBegrunnelseForRegistrering() {
        return begrunnelseForRegistrering;
    }

    public LocalDateTime getOpprettetDato() {
        return opprettetDato;
    }
}
