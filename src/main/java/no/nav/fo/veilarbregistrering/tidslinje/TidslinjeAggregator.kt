package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository

class TidslinjeAggregator(
        private val brukerRegistreringRepository: BrukerRegistreringRepository,
        private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository) {

    fun tidslinje(bruker: Bruker): List<TidslinjeElement> {

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringerFor(bruker.aktorId)
        val sykmeldtRegistreringer = sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(bruker.aktorId)

        val sykmeldtTidslinje = SykmeldtTidslinje(sykmeldtRegistreringer)
        val ordinaerRegistreringTidslinje = OrdinaerRegistreringTidslinje(ordinaerBrukerregistreringer)

        return sykmeldtTidslinje.tidslinje()
                .plus(ordinaerRegistreringTidslinje.tidslinje())
                .sortedBy { a -> a.periode() }
    }

}