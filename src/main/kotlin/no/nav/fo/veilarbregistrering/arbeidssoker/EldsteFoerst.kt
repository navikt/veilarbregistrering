package no.nav.fo.veilarbregistrering.arbeidssoker

import java.util.Comparator

class EldsteFoerst : Comparator<Formidlingsgruppeperiode> {
    override fun compare(t0: Formidlingsgruppeperiode, t1: Formidlingsgruppeperiode): Int {
        return t0.periode.compareTo(t1.periode)
    }
}