package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface FormidlingsgruppeRepository {
    fun lagre(event: FormidlingsgruppeEndretEvent): Long
    fun finnFormidlingsgrupperOgMapTilArbeidssokerperioder(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder
}