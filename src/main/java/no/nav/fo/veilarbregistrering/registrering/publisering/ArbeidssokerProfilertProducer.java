package no.nav.fo.veilarbregistrering.registrering.publisering;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;

import java.time.LocalDateTime;

public interface ArbeidssokerProfilertProducer {

    void publiserProfilering(AktorId aktorId, Innsatsgruppe innsatsgruppe, LocalDateTime profilertDato);
}
