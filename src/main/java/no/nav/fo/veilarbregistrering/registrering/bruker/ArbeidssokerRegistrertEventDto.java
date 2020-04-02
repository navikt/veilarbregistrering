package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

public class ArbeidssokerRegistrertEventDto {
    private final int rowNum;
    private final long bruker_registrering_id;
    private final AktorId aktor_id;
    private final String begrunnelse_for_registrering;
    private final LocalDateTime opprettet_dato;

    public ArbeidssokerRegistrertEventDto(
            int rowNum,
            long bruker_registrering_id,
            AktorId aktor_id,
            String begrunnelse_for_registrering,
            LocalDateTime opprettet_dato) {
        this.rowNum = rowNum;
        this.bruker_registrering_id = bruker_registrering_id;
        this.aktor_id = aktor_id;
        this.begrunnelse_for_registrering = begrunnelse_for_registrering;
        this.opprettet_dato = opprettet_dato;
    }

    public int getRowNum() {
        return rowNum;
    }

    public Long getBruker_registrering_id() {
        return bruker_registrering_id;
    }

    public AktorId getAktorId() {
        return aktor_id;
    }

    public String getBegrunnelseForRegistrering() {
        return begrunnelse_for_registrering;
    }

    public LocalDateTime getOpprettetDato() {
        return opprettet_dato;
    }
}
