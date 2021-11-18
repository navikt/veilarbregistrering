package no.nav.fo.veilarbregistrering.enhet

interface Adresse {
    val kommunenummer: Kommune?
    fun erGyldig(): Boolean
}