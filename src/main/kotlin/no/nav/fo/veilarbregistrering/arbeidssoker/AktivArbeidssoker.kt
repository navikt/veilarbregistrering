package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import java.time.LocalDateTime

data class AktivArbeidssoker(val fraDato: LocalDateTime): Tilstand {

    override fun håndter(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): Tilstand {
        return this
    }

    override fun håndter(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent): Tilstand {
        return if (formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            this
        } else {
            IkkeArbeidssoker(fraDato = formidlingsgruppeEndretEvent.hentFraDato())
        }
    }
}