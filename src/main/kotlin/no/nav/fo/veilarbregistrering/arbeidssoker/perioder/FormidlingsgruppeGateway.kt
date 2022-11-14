package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode

interface FormidlingsgruppeGateway {
    fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder
}