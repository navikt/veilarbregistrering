package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

open class GjelderFraService(
    private val gjelderFraRepository: GjelderFraRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
) {

    fun hentDato(bruker: Bruker): GjelderFraDato? {
        return try {
            gjelderFraRepository.hentDatoFor(bruker)
        } catch (e: Exception) {
            logger.warn("Feil ved henting av gjelder fra dato", e.message)
            null
        }
    }

    @Transactional
    open fun opprettDato(bruker: Bruker, dato: LocalDate) {
        val brukerRegistrering = hentOrdinaerBrukerRegistrering(bruker) ?: throw RuntimeException("Ingen brukerregistrering funnet")

        gjelderFraRepository.opprettDatoFor(bruker, brukerRegistrering.id, dato)
    }

    private fun hentOrdinaerBrukerRegistrering(bruker: Bruker): OrdinaerBrukerRegistrering? {
        return brukerRegistreringRepository
            .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
                bruker.aktorId, listOf(
                    Status.OVERFORT_ARENA,
                    Status.PUBLISERT_KAFKA,
                    Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND
                )
            )
            .firstOrNull()
    }
}
