package no.nav.fo.veilarbregistrering.arbeidsforhold

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold

interface ArbeidsforholdGateway {
    fun hentArbeidsforhold(fnr: Foedselsnummer): FlereArbeidsforhold
}