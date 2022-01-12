package no.nav.fo.veilarbregistrering.enhet

import java.util.*

data class Kommune (val kommunenummer: String) {

    fun kommuneMedBydeler(): Boolean = kommunenummer in KommuneMedBydel

    companion object {
        internal fun medBydel(kommuneMedBydel: KommuneMedBydel): Kommune {
            return Kommune(kommuneMedBydel.kommenummer)
        }
    }

    internal enum class KommuneMedBydel(val kommenummer: String) {
        OSLO("0301"), BERGEN("4601"), STAVANGER("1103"), TRONDHEIM("5001");

        companion object {
            operator fun contains(kommenummer: String): Boolean {
                return Arrays.stream(values())
                    .anyMatch { k: KommuneMedBydel -> k.kommenummer == kommenummer }
            }
        }
    }
}