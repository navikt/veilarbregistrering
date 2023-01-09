package no.nav.fo.veilarbregistrering.arbeidssoker

interface Observable {

    fun add(observer: Observer)
    fun remove(observer: Observer)
}