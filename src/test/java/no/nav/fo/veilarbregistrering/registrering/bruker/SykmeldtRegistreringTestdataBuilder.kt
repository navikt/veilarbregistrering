package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import java.time.LocalDateTime

object SykmeldtRegistreringTestdataBuilder {
    @JvmOverloads
    fun gyldigSykmeldtRegistrering(
        opprettetDato: LocalDateTime = LocalDateTime.now(),
        besvarelse: Besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(),
    ): SykmeldtRegistrering {
        return SykmeldtRegistrering(
                opprettetDato = opprettetDato,
                besvarelse = besvarelse,
                teksterForBesvarelse = TekstForSporsmalTestdataBuilder.gyldigeTeksterForSykmeldtBesvarelse(),
        )
    }
}