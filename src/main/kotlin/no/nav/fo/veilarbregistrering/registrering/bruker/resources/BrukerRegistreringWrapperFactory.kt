package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering

object BrukerRegistreringWrapperFactory {
    fun create(
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering?,
        sykmeldtBrukerRegistrering: SykmeldtRegistrering?
    ): BrukerRegistreringWrapper? =
        when {
            ordinaerBrukerRegistrering != null && sykmeldtBrukerRegistrering != null -> wrapNyesteRegistrering(
                ordinaerBrukerRegistrering,
                sykmeldtBrukerRegistrering
            )
            ordinaerBrukerRegistrering != null -> BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
            sykmeldtBrukerRegistrering != null -> BrukerRegistreringWrapper(sykmeldtBrukerRegistrering)
            else -> null
        }

    private fun wrapNyesteRegistrering(
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering,
        sykmeldtBrukerRegistrering: SykmeldtRegistrering
    ): BrukerRegistreringWrapper {
        val profilertBrukerRegistreringDato = ordinaerBrukerRegistrering.opprettetDato
        val sykmeldtRegistreringDato = sykmeldtBrukerRegistrering.opprettetDato
        return if (profilertBrukerRegistreringDato.isAfter(sykmeldtRegistreringDato)) {
            BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        } else {
            BrukerRegistreringWrapper(sykmeldtBrukerRegistrering)
        }
    }
}