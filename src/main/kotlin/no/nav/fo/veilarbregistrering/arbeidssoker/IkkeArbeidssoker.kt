package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import java.time.LocalDateTime

data class IkkeArbeidssoker(val fraDato: LocalDateTime? = null) : Tilstand {

    override fun håndter(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): Tilstand {
        return AktivArbeidssoker(fraDato = ordinaerBrukerRegistrering.hentFraDato())
    }

    override fun håndter(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent): Tilstand {
        return if (formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            AktivArbeidssoker(fraDato = formidlingsgruppeEndretEvent.hentFraDato())
        } else {
            this
        }
    }
}