package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering

sealed interface Tilstand {
    fun håndter(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): Tilstand
    fun håndter(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent): Tilstand
    fun håndter(reaktivering: Reaktivering): Tilstand

}