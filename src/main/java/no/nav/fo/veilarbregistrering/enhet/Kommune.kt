package no.nav.fo.veilarbregistrering.enhet

data class Kommune (val kommunenummer: String) {

    fun kommuneMedBydeler(): Boolean = kommunenummer in KommuneMedBydel

    companion object {
        internal fun of(kommuneMedBydel: KommuneMedBydel): Kommune {
            return Kommune(kommuneMedBydel.kommenummer)
        }
    }
}