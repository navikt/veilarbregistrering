package no.nav.fo.veilarbregistrering.registrering.ordinaer

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.Stilling
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import no.nav.fo.veilarbregistrering.registrering.veileder.Veileder
import java.time.LocalDateTime

data class OrdinaerBrukerRegistrering(
    override val id: Long = 0,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val besvarelse: Besvarelse,
    val teksterForBesvarelse: List<TekstForSporsmal>,
    val sisteStilling: Stilling,
    val profilering: Profilering? = null,
    override var manueltRegistrertAv: Veileder? = null,
) : BrukerRegistrering() {

    override fun hentType(): BrukerRegistreringType {
        return BrukerRegistreringType.ORDINAER
    }

    override fun toString(): String {
        return "OrdinaerBrukerRegistrering(id=$id, opprettetDato=$opprettetDato, besvarelse=$besvarelse, teksterForBesvarelse=$teksterForBesvarelse, sisteStilling=$sisteStilling, profilering=$profilering)"
    }

    fun med(profilering: Profilering) : OrdinaerBrukerRegistrering {
        return OrdinaerBrukerRegistrering(
            id,
            opprettetDato,
            besvarelse,
            teksterForBesvarelse,
            sisteStilling,
            profilering,
            manueltRegistrertAv
        )
    }
}