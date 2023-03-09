package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.springframework.stereotype.Service

@Service
class ArbeidssokerService(
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService
) {

    fun hentArbeidssokerperioder(bruker: Bruker, forespurtPeriode: Periode?): Arbeidssokerperioder {
        val lagredePerioder = arbeidssokerperiodeService.hentPerioder(bruker)

        forespurtPeriode?.let {
            return Arbeidssokerperioder.of(lagredePerioder.filter { it.overlapperMed(forespurtPeriode) }.map { Arbeidssokerperiode(it) })
        }

        return Arbeidssokerperioder.of(lagredePerioder.map { Arbeidssokerperiode(it) })
    }

}
