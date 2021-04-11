package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Periode

interface TidslinjeElement {

    fun periode() : Periode

    fun status() : Status

    fun kilde() : Kilde

}
