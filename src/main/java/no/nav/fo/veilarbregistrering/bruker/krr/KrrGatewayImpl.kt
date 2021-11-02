package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer

internal class KrrGatewayImpl(private val krrClient: KrrClient) : KrrGateway {
    override fun hentKontaktinfo(bruker: Bruker): Telefonnummer? {
        return krrClient.hentKontaktinfo(bruker.gjeldendeFoedselsnummer)
            ?.let { Telefonnummer.of(it.mobiltelefonnummer) }
    }
}