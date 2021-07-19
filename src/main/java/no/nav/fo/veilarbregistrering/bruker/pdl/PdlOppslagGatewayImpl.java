package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.feil.HentIdenterException;
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagMapper.map;
import static no.nav.fo.veilarbregistrering.config.CacheConfig.*;

class PdlOppslagGatewayImpl implements PdlOppslagGateway {

    private final static Logger LOG = LoggerFactory.getLogger(PdlOppslagGatewayImpl.class);

    private final PdlOppslagClient pdlOppslagClient;

    PdlOppslagGatewayImpl(PdlOppslagClient pdlOppslagClient) {
        this.pdlOppslagClient = pdlOppslagClient;
    }

    @Override
    @Cacheable(HENT_PERSON_FOR_AKTORID)
    public Optional<Person> hentPerson(AktorId aktorid) {
        try {
            PdlPerson pdlPerson = pdlOppslagClient.hentPerson(aktorid);
            return Optional.of(map(pdlPerson));
        } catch (BrukerIkkeFunnetException e) {
            LOG.warn("Hent person gav ikke treff", e);
            return Optional.empty();
        }
    }

    @Cacheable(HENT_GEOGRAFISK_TILKNYTNING)
    @Override
    public Optional<GeografiskTilknytning> hentGeografiskTilknytning(AktorId aktorId) {
        try {
            PdlGeografiskTilknytning pdlGeografiskTilknytning = pdlOppslagClient.hentGeografiskTilknytning(aktorId);
            return Optional.ofNullable(PdlOppslagMapper.map(pdlGeografiskTilknytning));
        } catch (BrukerIkkeFunnetException e) {
            LOG.warn("Hent geografisk tilknytning gav ikke treff", e);
            return Optional.empty();
        } catch (Exception e) {
            LOG.warn("Ukjent feil ved henting av geografisk tilknytning", e);
            return Optional.empty();
        }
    }

    @Override
    @Cacheable(HENT_PERSONIDENTER)
    public Identer hentIdenter(Foedselsnummer fnr) {
        try {
            PdlIdenter pdlIdenter = pdlOppslagClient.hentIdenter(fnr);
            return PdlOppslagMapper.map(pdlIdenter);
        } catch (BrukerIkkeFunnetException e) {
            throw e;
        } catch (RuntimeException e) {
            LOG.error("HentIdenterFeilet: ", e);
            throw new HentIdenterException(e.getMessage());
        }
    }

    @Override
    public Identer hentIdenter(AktorId aktorId) {
        try {
            PdlIdenter pdlIdenter = pdlOppslagClient.hentIdenter(aktorId);
            return PdlOppslagMapper.map(pdlIdenter);
        } catch (BrukerIkkeFunnetException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new HentIdenterException(e.getMessage());
        }
    }
}
