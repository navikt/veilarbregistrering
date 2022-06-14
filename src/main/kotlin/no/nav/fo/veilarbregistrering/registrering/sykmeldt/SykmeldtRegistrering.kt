package no.nav.fo.veilarbregistrering.registrering.sykmeldt

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder
import java.time.LocalDateTime

data class SykmeldtRegistrering(
    override val id: Long = 0,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val besvarelse: Besvarelse,
    val teksterForBesvarelse: List<TekstForSporsmal>,
    override var manueltRegistrertAv: Veileder? = null,
) : BrukerRegistrering() {
    override fun hentType(): BrukerRegistreringType {
        return BrukerRegistreringType.SYKMELDT
    }

    override fun toString(): String {
        return "SykmeldtRegistrering(id=" + id + ", opprettetDato=" + opprettetDato + ", besvarelse=" + besvarelse + ", teksterForBesvarelse=" + teksterForBesvarelse + ")"
    }
}