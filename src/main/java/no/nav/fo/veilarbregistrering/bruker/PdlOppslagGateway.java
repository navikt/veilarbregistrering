package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public interface PdlOppslagGateway {

    Optional<Person> hentPerson(AktorId aktorid);

    Identer hentIdenter(Foedselsnummer fnr);

    Identer hentIdenter(AktorId aktorId);
}
