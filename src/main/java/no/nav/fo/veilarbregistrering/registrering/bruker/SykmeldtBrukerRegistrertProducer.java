package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

public interface SykmeldtBrukerRegistrertProducer {

    void publiserArbeidssokerRegistrert(
            AktorId aktorId,
            DinSituasjonSvar brukersSituasjon,
            LocalDateTime opprettetDato);
}
