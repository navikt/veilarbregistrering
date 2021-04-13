package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder
import java.time.LocalDateTime

object OrdinaerBrukerRegistreringTestdataBuilder {

    @JvmStatic
    fun gyldigBrukerRegistrering(): OrdinaerBrukerRegistrering {
        return gyldigBrukerRegistrering(LocalDateTime.now())
    }

    @JvmStatic
    fun gyldigBrukerRegistrering(opprettetDato: LocalDateTime): OrdinaerBrukerRegistrering {
        return OrdinaerBrukerRegistrering()
                .setOpprettetDato(opprettetDato)
                .setSisteStilling(StillingTestdataBuilder.gyldigStilling())
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse())
                .setTeksterForBesvarelse(TekstForSporsmalTestdataBuilder.gyldigeTeksterForBesvarelse())
    }

    @JvmStatic
    fun gyldigBrukerRegistreringUtenJobb(): OrdinaerBrukerRegistrering {
        return gyldigBrukerRegistrering().setSisteStilling(
                StillingTestdataBuilder.ingenYrkesbakgrunn()
        ).setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        )
    }
}