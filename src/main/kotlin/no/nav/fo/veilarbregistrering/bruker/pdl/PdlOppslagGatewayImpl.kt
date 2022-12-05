package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.feil.HentIdenterException
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagMapper.map
import no.nav.fo.veilarbregistrering.config.CacheConfig
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable

open class PdlOppslagGatewayImpl(private val pdlOppslagClient: PdlOppslagClient) : PdlOppslagGateway {

    @Cacheable(CacheConfig.HENT_PERSON_FOR_AKTORID)
    override fun hentPerson(aktorid: AktorId): Person? {
        return try {
            val pdlPerson = pdlOppslagClient.hentPerson(aktorid)
            map(pdlPerson)
        } catch (e: BrukerIkkeFunnetException) {
            LOG.warn("Hent person gav ikke treff", e)
            null
        }
    }

    @Cacheable(CacheConfig.HENT_GEOGRAFISK_TILKNYTNING)
    override fun hentGeografiskTilknytning(aktorId: AktorId): GeografiskTilknytning? {
        return try {
            val pdlGeografiskTilknytning = pdlOppslagClient.hentGeografiskTilknytning(aktorId)
            map(pdlGeografiskTilknytning)
        } catch (e: BrukerIkkeFunnetException) {
            LOG.warn("Hent geografisk tilknytning gav ikke treff", e)
            null
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

    override fun hentIdenterBolk(aktorIder: List<AktorId>): Map<AktorId, Foedselsnummer> {
        val pdlIdenterBolk = pdlOppslagClient.hentIdenterBolk(aktorIder)
        return pdlIdenterBolk
            .filter { it.identer != null }
            .associate {
                AktorId(it.ident) to Foedselsnummer(
                    it.identer?.first()?.ident
                        ?: throw BrukerIkkeFunnetException("Fant ikke fnr for aktørid ${it.ident}")
                )
            }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PdlOppslagGatewayImpl::class.java)
    }
}
