package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.logger
import org.slf4j.LoggerFactory

class FormidlingsgruppeGatewayImpl(
    private val formidlingsgruppeRestClient: FormidlingsgruppeRestClient,
    private val unleashClient: UnleashClient
) :
    FormidlingsgruppeGateway {
    override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
        if (unleashClient.isEnabled("veilarbregistrering.formidlingshistorikk_v2")) {
            LOG.info("Henter formidlingshistorikk via ny client")

            val formidlingsgruppeResponseDto =
                formidlingsgruppeRestClient.hentFormidlingshistorikkVersjon2(foedselsnummer, periode)
            logger.info("Fikk følgende arbeidssokerperioder fra Arena sin ORDS-tjeneste: $formidlingsgruppeResponseDto")

            val arbeidssokerperioderFraVersjon2: List<Arbeidssokerperiode> =
                formidlingsgruppeResponseDto
                    ?.let(FormidlingshistorikkMapper::hentArbeidssokerperioderOgMap)
                    ?: emptyList()

            return Arbeidssokerperioder(arbeidssokerperioderFraVersjon2)
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