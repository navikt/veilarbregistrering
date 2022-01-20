package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer
import org.slf4j.LoggerFactory

internal class KrrGatewayImpl(
    private val krrClient: KrrClient,
    private val digdirKrrProxyClient: DigDirKrrProxyClient,
    private val unleashClient: UnleashClient) : KrrGateway
{
    override fun hentKontaktinfo(bruker: Bruker): Telefonnummer? {

        val telefonnummer = krrClient.hentKontaktinfo(bruker.gjeldendeFoedselsnummer)
            ?.let { Telefonnummer.of(it.mobiltelefonnummer) }

        LOG.info("Henter kontaktinfo fra KrrClient")

        if (digdirKrrProxyEnabled()) {
            try {
                LOG.info("Henter kontaktinfo fra DigDirKrrProxy")

                digdirKrrProxyClient.hentKontaktinfo(bruker.gjeldendeFoedselsnummer)
                    ?.let { Telefonnummer.of(it.mobiltelefonnummer) }

            } catch (e: RuntimeException) {
                LOG.error("Kall mot DigDirKrrProxy feilet", e)
            }
        }

        return telefonnummer
    }

    private fun digdirKrrProxyEnabled(): Boolean {
        return unleashClient.isEnabled("veilarbregistrering.enable.digdirkrrproxy")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KrrGatewayImpl::class.java)
    }
}