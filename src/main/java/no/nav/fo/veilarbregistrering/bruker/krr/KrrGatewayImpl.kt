package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer
import org.slf4j.LoggerFactory

internal class KrrGatewayImpl(private val digdirKrrProxyClient: DigDirKrrProxyClient) : KrrGateway
{
    override fun hentKontaktinfo(bruker: Bruker): Telefonnummer? {
        LOG.info("Henter kontaktinfo fra DigDirKrrProxy")

        return digdirKrrProxyClient.hentKontaktinfo(bruker.gjeldendeFoedselsnummer)
            ?.let { Telefonnummer.of(it.mobiltelefonnummer) }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KrrGatewayImpl::class.java)
    }
}