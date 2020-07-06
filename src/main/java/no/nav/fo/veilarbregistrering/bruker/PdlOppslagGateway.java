package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public interface PdlOppslagGateway {

    Optional<Person> hentPerson(AktorId aktorid);

    Optional<Identer> hentIdenter(Foedselsnummer fnr);
}
