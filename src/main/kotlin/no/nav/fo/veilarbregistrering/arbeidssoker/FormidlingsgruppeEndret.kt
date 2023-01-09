package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe

interface FormidlingsgruppeEndret: EndreArbeidssøker {
    fun formidlingsgruppe(): Formidlingsgruppe
}
