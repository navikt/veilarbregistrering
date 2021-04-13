package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository

class TidslinjeAggregator(
        private val brukerRegistreringRepository: BrukerRegistreringRepository,
        private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        private val reaktiveringRepository: ReaktiveringRepository) {

    fun tidslinje(bruker: Bruker): List<TidslinjeElement> {

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringerFor(bruker.aktorId)
        val sykmeldtRegistreringer = sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(bruker.aktorId)
        val reaktiveringer = reaktiveringRepository.finnReaktiveringer(bruker.aktorId)

        val sykmeldtTidslinje = SykmeldtTidslinje(sykmeldtRegistreringer)
        val ordinaerRegistreringTidslinje = OrdinaerRegistreringTidslinje(ordinaerBrukerregistreringer)
        val reaktiveringTidslinje = ReaktiveringTidslinje(reaktiveringer)

        return sykmeldtTidslinje.tidslinje()
                .plus(ordinaerRegistreringTidslinje.tidslinje())
                .plus(reaktiveringTidslinje.tidslinje())
                .sortedBy { e -> e.periode().fra }
    }

}