package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering

interface ArbeidssokerState {
    fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering)
    fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering)
    fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent)
    fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent)
}