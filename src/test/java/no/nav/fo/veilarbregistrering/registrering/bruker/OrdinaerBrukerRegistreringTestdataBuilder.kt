package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import java.time.LocalDateTime

object OrdinaerBrukerRegistreringTestdataBuilder {

    @JvmStatic
    fun gyldigBrukerRegistrering(
        opprettetDato: LocalDateTime = LocalDateTime.now(),
        stilling: Stilling = StillingTestdataBuilder.gyldigStilling(),
        besvarelse: Besvarelse = BesvarelseTestdataBuilder.gyldigBesvarelse(),
        teksterForBesvarelse: List<TekstForSporsmal> = TekstForSporsmalTestdataBuilder.gyldigeTeksterForBesvarelse(),
        profilering: Profilering? = null,
    ): OrdinaerBrukerRegistrering {
        return OrdinaerBrukerRegistrering(
                opprettetDato = opprettetDato,
                sisteStilling = stilling,
                besvarelse = besvarelse,
                teksterForBesvarelse = teksterForBesvarelse,
                profilering = profilering,
        )
    }

    @JvmStatic
    fun gyldigBrukerRegistreringUtenJobb(opprettetDato: LocalDateTime = LocalDateTime.now()): OrdinaerBrukerRegistrering {
        return gyldigBrukerRegistrering(
            opprettetDato = opprettetDato,
            stilling = StillingTestdataBuilder.ingenYrkesbakgrunn(),
            besvarelse = BesvarelseTestdataBuilder.gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.ALDRI_HATT_JOBB,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
            )
        )
    }
}