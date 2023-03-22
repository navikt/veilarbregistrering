package no.nav.fo.veilarbregistrering.registrering.publisering

interface ArbeidssokerRegistrertProducerV2 {
    fun publiserArbeidssokerRegistrert(event: ArbeidssokerRegistrertInternalEventV2): Boolean
}