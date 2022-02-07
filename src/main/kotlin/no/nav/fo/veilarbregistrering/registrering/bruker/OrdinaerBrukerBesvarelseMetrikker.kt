package no.nav.fo.veilarbregistrering.registrering.bruker

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.profilering.Profilering

internal object OrdinaerBrukerBesvarelseMetrikker {
    @JvmStatic
    fun rapporterOrdinaerBesvarelse(
        prometheusMetricsService: PrometheusMetricsService,
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering,
        profilering: Profilering
    ) {
        val helseHinderSvar = ordinaerBrukerRegistrering.besvarelse.helseHinder
        val andreForholdSvar = ordinaerBrukerRegistrering.besvarelse.andreForhold
        val samsvarermedinfofraaareg =
            samsvarerBesvarelseMedAaregVedrArbeidSisteTolvMaaneder(ordinaerBrukerRegistrering, profilering)

        prometheusMetricsService.registrer(Events.ORDINAER_BESVARELSE)

        if (helseHinderSvar != null && helseHinderSvar != HelseHinderSvar.INGEN_SVAR) {
            prometheusMetricsService.registrer(
                Events.BESVARELSE_HELSEHINDER,
                Tag.of("helsehinder", helseHinderSvar.name )
            )
        }

        if (andreForholdSvar != null && andreForholdSvar != AndreForholdSvar.INGEN_SVAR) {
            prometheusMetricsService.registrer(
                Events.BESVARELSE_ANDRE_FORHOLD,
                Tag.of("andre_forhold", andreForholdSvar.name)
            )
        }

        prometheusMetricsService.registrer(
            Events.BESVARELSE_HAR_HATT_JOBB_SAMSVARER_M_AAREG,
            Tag.of("verdi", samsvarermedinfofraaareg.toString())
        )


    }

    private fun samsvarerBesvarelseMedAaregVedrArbeidSisteTolvMaaneder(
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering,
        profilering: Profilering
    ) = ((svarSomIndikererArbeidSisteManeder.contains(ordinaerBrukerRegistrering.besvarelse.dinSituasjon) ||
            ordinaerBrukerRegistrering.besvarelse.sisteStilling == SisteStillingSvar.HAR_HATT_JOBB)
            == profilering.jobbetSammenhengendeSeksAvTolvSisteManeder)

    private val svarSomIndikererArbeidSisteManeder = listOf(
        DinSituasjonSvar.MISTET_JOBBEN,
        DinSituasjonSvar.HAR_SAGT_OPP,
        DinSituasjonSvar.ER_PERMITTERT,
        DinSituasjonSvar.DELTIDSJOBB_VIL_MER,
        DinSituasjonSvar.VIL_BYTTE_JOBB,
        DinSituasjonSvar.VIL_FORTSETTE_I_JOBB
    )

}