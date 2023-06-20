package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.arbeid.soker.periode.ArbeidssokerperiodeEvent

interface ArbeidssokerperiodeProducer {
    fun publiserArbeidssokerperiode(event: ArbeidssokerperiodeEvent): Boolean
}