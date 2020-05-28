package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

import java.time.LocalDate;

public interface OppgaveGateway {

    Oppgave opprettOppgave(
            AktorId aktoerId,
            Enhetsnr enhetsnr,
            String beskrivelse,
            LocalDate fristFerdigstillelse,
            LocalDate aktivDato);

}
