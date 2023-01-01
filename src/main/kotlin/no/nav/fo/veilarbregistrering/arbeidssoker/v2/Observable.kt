package no.nav.fo.veilarbregistrering.arbeidssoker.v2

interface Observable {

    fun add(observer: Observer)
    fun remove(observer: Observer)
}