package no.nav.fo.veilarbregistrering.registrering.publisering

interface ArbeidssokerRegistrertProducer {
    fun publiserArbeidssokerRegistrert(event: ArbeidssokerRegistrertInternalEvent?): Boolean
}