package no.nav.fo.veilarbregistrering.oppfolging.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil

class OppfolgingClientMock internal constructor() : OppfolgingClient(
    mockk(relaxed = true),
    mockk(relaxed = true),
    "",
    mockk(relaxed = true),
    { "TOKEN" }
) {

    override fun aktiverBruker(aktiverBrukerData: AktiverBrukerData) {
        //sendException("BRUKER_ER_UKJENT");
        //sendException("BRUKER_KAN_IKKE_REAKTIVERES");
        //sendException("BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET");
        //sendException("BRUKER_MANGLER_ARBEIDSTILLATELSE");
    }

    override fun reaktiverBruker(fnr: Fnr) {}
    private fun sendException(feilType: String) {
        throw AktiverBrukerException("Feil ved reaktivering av bruker", AktiverBrukerFeil.valueOf(feilType))
    }
}