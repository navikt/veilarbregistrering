package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering

object BrukerRegistreringWrapperFactory {
    @JvmStatic
    fun create(
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering?,
        sykmeldtBrukerRegistrering: SykmeldtRegistrering?
    ): BrukerRegistreringWrapper? {
        if (ordinaerBrukerRegistrering == null && sykmeldtBrukerRegistrering == null) {
            return null
        } else if (ordinaerBrukerRegistrering == null) {
            return BrukerRegistreringWrapper(sykmeldtBrukerRegistrering)
        } else if (sykmeldtBrukerRegistrering == null) {
            return BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        }
        val profilertBrukerRegistreringDato = ordinaerBrukerRegistrering.opprettetDato
        val sykmeldtRegistreringDato = sykmeldtBrukerRegistrering.opprettetDato
        return if (profilertBrukerRegistreringDato.isAfter(sykmeldtRegistreringDato)) {
            BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        } else {
            BrukerRegistreringWrapper(sykmeldtBrukerRegistrering)
        }
    }
}