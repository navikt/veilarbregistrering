package no.nav.fo.veilarbregistrering.arbeidssoker

interface Observer {

    fun update(event: ArbeidssøkerDomainEvent)
}