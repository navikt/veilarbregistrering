package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;

import static no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagMapper.map;

class PdlOppslagGatewayImpl implements PdlOppslagGateway {

    private final PdlOppslagClient pdlOppslagClient;

    PdlOppslagGatewayImpl(PdlOppslagClient pdlOppslagClient) {
        this.pdlOppslagClient = pdlOppslagClient;
    }

    @Override
    public Person hentPerson(AktorId aktorid) {
        return map(pdlOppslagClient.hentPerson(aktorid));
    }
}
