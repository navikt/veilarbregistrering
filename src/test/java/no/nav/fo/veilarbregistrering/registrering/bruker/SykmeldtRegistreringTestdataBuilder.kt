package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import java.time.LocalDateTime

object SykmeldtRegistreringTestdataBuilder {
    @JvmOverloads
    fun gyldigSykmeldtRegistrering(opprettetDato: LocalDateTime? = LocalDateTime.now()): SykmeldtRegistrering {
        return SykmeldtRegistrering()
                .setOpprettetDato(opprettetDato)
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse())
                .setTeksterForBesvarelse(TekstForSporsmalTestdataBuilder.gyldigeTeksterForSykmeldtBesvarelse())
    }
}