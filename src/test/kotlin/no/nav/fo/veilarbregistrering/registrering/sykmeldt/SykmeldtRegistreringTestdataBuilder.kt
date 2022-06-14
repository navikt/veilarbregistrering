package no.nav.fo.veilarbregistrering.registrering.sykmeldt

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmalTestdataBuilder
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