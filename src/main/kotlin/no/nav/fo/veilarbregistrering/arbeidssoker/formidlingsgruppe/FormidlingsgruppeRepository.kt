package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface FormidlingsgruppeRepository {
    fun lagre(event: FormidlingsgruppeEndretEvent): Long
    fun finnFormidlingsgruppeEndretEventFor(foedselsnummerList: List<Foedselsnummer>): List<FormidlingsgruppeEndretEvent>
}