package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.feil.HentIdenterException
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagMapper.map
import no.nav.fo.veilarbregistrering.config.CacheConfig
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import java.util.*

open class PdlOppslagGatewayImpl(private val pdlOppslagClient: PdlOppslagClient) : PdlOppslagGateway {

    @Cacheable(CacheConfig.HENT_PERSON_FOR_AKTORID)
    override fun hentPerson(aktorid: AktorId): Optional<Person> {
        return try {
            val pdlPerson = pdlOppslagClient.hentPerson(aktorid)
            Optional.of(map(pdlPerson))
        } catch (e: BrukerIkkeFunnetException) {
            LOG.warn("Hent person gav ikke treff", e)
            Optional.empty()
        }
    }

    @Cacheable(CacheConfig.HENT_GEOGRAFISK_TILKNYTNING)
    override fun hentGeografiskTilknytning(aktorId: AktorId): Optional<GeografiskTilknytning> {
        return try {
            val pdlGeografiskTilknytning = pdlOppslagClient.hentGeografiskTilknytning(aktorId)
            Optional.ofNullable(map(pdlGeografiskTilknytning))
        } catch (e: BrukerIkkeFunnetException) {
            LOG.warn("Hent geografisk tilknytning gav ikke treff", e)
            Optional.empty()
        } catch (e: Exception) {
            LOG.warn("Ukjent feil ved henting av geografisk tilknytning", e)
            Optional.empty()
        }
    }

    @Cacheable(CacheConfig.HENT_PERSONIDENTER)
    override fun hentIdenter(fnr: Foedselsnummer): Identer {
        return try {
            val pdlIdenter = pdlOppslagClient.hentIdenter(fnr)
            map(pdlIdenter)
        } catch (e: BrukerIkkeFunnetException) {
            throw e
        } catch (e: RuntimeException) {
            LOG.error("HentIdenterFeilet: ", e)
            throw HentIdenterException(e.message!!)
        }
    }

    override fun hentIdenter(aktorId: AktorId): Identer {
        return try {
            val pdlIdenter = pdlOppslagClient.hentIdenter(aktorId)
            map(pdlIdenter)
        } catch (e: BrukerIkkeFunnetException) {
            throw e
        } catch (e: RuntimeException) {
            throw HentIdenterException(e.message!!)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PdlOppslagGatewayImpl::class.java)
    }
}
