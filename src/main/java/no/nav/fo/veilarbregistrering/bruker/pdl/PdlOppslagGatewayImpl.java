package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;

import java.util.Optional;

class PdlOppslagGatewayImpl implements PdlOppslagGateway {

    private PdlOppslagClient pdlOppslagClient;

    PdlOppslagGatewayImpl(PdlOppslagClient pdlOppslagClient) {
        this.pdlOppslagClient = pdlOppslagClient;
    }

    @Override
    public Person hentPerson(AktorId aktorid) {
        return map(pdlOppslagClient.hentPerson(aktorid));
    }

    private Person map(Optional<PdlPerson> person) {
        return person.map(pdlPerson ->
            Person.of(
                    pdlPerson.getSisteOpphold().map(PdlPersonOpphold::getType).map(Enum::toString).orElse(null),
                    pdlPerson.getSisteStatsborgerskap().map(PdlStatsborgerskap::getLand).orElse(null))
        ).orElse(null);
    }
}
