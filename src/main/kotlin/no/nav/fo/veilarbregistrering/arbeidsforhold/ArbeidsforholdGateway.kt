package no.nav.fo.veilarbregistrering.arbeidsforhold

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface ArbeidsforholdGateway {
    fun hentArbeidsforhold(fnr: Foedselsnummer): FlereArbeidsforhold
}