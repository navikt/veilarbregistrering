package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status

class TidslinjeAggregator(
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    private val reaktiveringRepository: ReaktiveringRepository,
    private val arbeidssokerRepository: ArbeidssokerRepository) {

    fun tidslinje(bruker: Bruker): List<TidslinjeElement> {

        val ordinaerBrukerregistreringer = brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.aktorId, listOf(Status.OVERFORT_ARENA))
        val sykmeldtRegistreringer = sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(bruker.aktorId)
        val reaktiveringer = reaktiveringRepository.finnReaktiveringer(bruker.aktorId)
        val arbeidssokerperioder = arbeidssokerRepository.finnFormidlingsgrupper(bruker.alleFoedselsnummer())

        val sykmeldtTidslinje = SykmeldtTidslinje(sykmeldtRegistreringer)
        val ordinaerRegistreringTidslinje = OrdinaerRegistreringTidslinje(ordinaerBrukerregistreringer)
        val reaktiveringTidslinje = ReaktiveringTidslinje(reaktiveringer)
        val arbeidssokerperiodeTidslinje = ArbeidssokerperiodeTidslinje(arbeidssokerperioder.asList())

        return sykmeldtTidslinje.tidslinje()
                .plus(ordinaerRegistreringTidslinje.tidslinje())
                .plus(reaktiveringTidslinje.tidslinje())
                .plus(arbeidssokerperiodeTidslinje.tidslinje())
                .sortedBy { e -> e.periode().fra }
    }

}