package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface KontaktBrukerHenvendelseProducer {

    void publiserHenvendelse(AktorId aktorId);
}
