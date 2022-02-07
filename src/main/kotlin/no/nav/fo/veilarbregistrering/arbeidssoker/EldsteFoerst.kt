package no.nav.fo.veilarbregistrering.arbeidssoker

import java.util.Comparator

class EldsteFoerst : Comparator<Arbeidssokerperiode> {
    override fun compare(t0: Arbeidssokerperiode, t1: Arbeidssokerperiode): Int {
        return t0.periode.compareTo(t1.periode)
    }
}