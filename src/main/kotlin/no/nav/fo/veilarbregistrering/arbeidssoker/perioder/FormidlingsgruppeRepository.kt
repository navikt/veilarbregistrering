package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface FormidlingsgruppeRepository {
    fun lagre(event: FormidlingsgruppeEvent): Long
    fun finnFormidlingsgrupperOgMapTilArbeidssokerperioder(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder
}