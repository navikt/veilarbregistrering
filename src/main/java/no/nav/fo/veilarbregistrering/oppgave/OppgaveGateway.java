package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

public interface OppgaveGateway {

    Oppgave opprettOppgave(AktorId aktoerId, String beskrivelse, Enhetsnr enhetsnr);

}
