package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

public class ArbeidssokerRegistrertEventDto {
    private final AktorId aktorId;
    private final String begrunnelseForRegistrering;
    private final String utdanningGodkjent;
    private final String utdanningBestatt;
    private final LocalDateTime opprettetDato;

    public ArbeidssokerRegistrertEventDto(
            AktorId aktorId,
            String begrunnelseForRegistrering,
            String utdanningGodkjent,
            String utdanningBestatt,
            LocalDateTime opprettetDato) {
        this.aktorId = aktorId;
        this.begrunnelseForRegistrering = begrunnelseForRegistrering;
        this.utdanningGodkjent = utdanningGodkjent;
        this.utdanningBestatt = utdanningBestatt;
        this.opprettetDato = opprettetDato;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public LocalDateTime getOpprettetDato() {
        return opprettetDato;
    }

    public Besvarelse getBesvarelse() {
        return new Besvarelse()
                .setDinSituasjon(begrunnelseForRegistrering != null ? DinSituasjonSvar.valueOf(begrunnelseForRegistrering) : null)
                .setUtdanningBestatt(utdanningBestatt != null ? UtdanningBestattSvar.valueOf(utdanningBestatt) : null)
                .setUtdanningGodkjent(utdanningGodkjent != null ? UtdanningGodkjentSvar.valueOf(utdanningGodkjent) : null);
    }
}
