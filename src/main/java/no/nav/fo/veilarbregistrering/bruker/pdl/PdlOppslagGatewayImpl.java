package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagMapper.map;

class PdlOppslagGatewayImpl implements PdlOppslagGateway {

    private final static Logger LOG = LoggerFactory.getLogger(PdlOppslagGatewayImpl.class);

    private final PdlOppslagClient pdlOppslagClient;

    PdlOppslagGatewayImpl(PdlOppslagClient pdlOppslagClient) {
        this.pdlOppslagClient = pdlOppslagClient;
    }

    @Override
    public Optional<Person> hentPerson(AktorId aktorid) {
        try {
            PdlPerson pdlPerson = pdlOppslagClient.hentPerson(aktorid);
            return Optional.of(map(pdlPerson));
        } catch (BrukerIkkeFunnetException e) {
            LOG.warn("Hent person gav ikke treff", e);
            return Optional.empty();
        }
    }
}
