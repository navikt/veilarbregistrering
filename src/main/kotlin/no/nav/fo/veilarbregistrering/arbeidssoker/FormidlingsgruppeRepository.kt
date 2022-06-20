package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface FormidlingsgruppeRepository {
    fun lagre(command: EndretFormidlingsgruppeCommand): Long
    fun finnFormidlingsgrupper(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder
}