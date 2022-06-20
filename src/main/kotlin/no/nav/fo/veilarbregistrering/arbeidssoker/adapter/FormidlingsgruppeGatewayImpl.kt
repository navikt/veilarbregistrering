package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.slf4j.LoggerFactory

class FormidlingsgruppeGatewayImpl(
    private val formidlingsgruppeRestClient: FormidlingsgruppeRestClient,
    private val unleashClient: UnleashClient
) :
    FormidlingsgruppeGateway {
    override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
        if (unleashClient.isEnabled("veilarbregistrering.formidlingshistorikk_v2")) {
            try {
                LOG.info("Henter formidlingshistorikk via ny client")

                val arbeidssokerperioderFraVersjon2: List<Arbeidssokerperiode> =
                    formidlingsgruppeRestClient.hentFormidlingshistorikkVersjon2(foedselsnummer, periode)
                        ?.let(FormidlingshistorikkMapper::hentArbeidssokerperioderOgMap) ?: emptyList()

                LOG.info("Returnerer formidlingshistorikk fra ny client")

                return Arbeidssokerperioder(arbeidssokerperioderFraVersjon2)

            } catch (e : RuntimeException) {
                LOG.warn("Integrasjon mot ARENA ORDS feilet. Har ikke betydning for flyt.", e)
            }
        }

        val arbeidssokerperioder: List<Arbeidssokerperiode> =
            formidlingsgruppeRestClient.hentFormidlingshistorikk(foedselsnummer, periode)
                ?.let(FormidlingshistorikkMapper::hentArbeidssokerperioderOgMap) ?: emptyList()

        return Arbeidssokerperioder(arbeidssokerperioder)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FormidlingsgruppeGatewayImpl::class.java)
    }
}