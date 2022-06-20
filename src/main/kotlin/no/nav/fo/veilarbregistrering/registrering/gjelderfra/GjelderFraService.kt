package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GjelderFraService(
    private val gjelderFraRepository: GjelderFraRepository
) {

    fun hentDato(bruker: Bruker): GjelderFraDato? {
        return gjelderFraRepository.hentDatoFor(bruker)
    }

    @Transactional
    fun opprettDato(bruker: Bruker, brukerRegistrering: BrukerRegistrering, dato: LocalDate): GjelderFraDato? {
        return gjelderFraRepository.opprettDatoFor(bruker, brukerRegistrering, dato)
    }

}
