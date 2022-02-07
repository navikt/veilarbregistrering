package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException

class Identer(val identer: List<Ident>) {
    fun finnGjeldendeFnr(): Foedselsnummer {
        val gjeldendeFnr = identer
            .filter { it.gruppe == Gruppe.FOLKEREGISTERIDENT }
            .firstOrNull { !it.isHistorisk }
            ?: throw ManglendeBrukerInfoException("Kunne ikke finne et gjeldende fødselsnummer")
        return Foedselsnummer(gjeldendeFnr.ident)
    }

    fun finnGjeldendeAktorId(): AktorId {
        val gjeldendeAktorId = identer
            .filter { it.gruppe == Gruppe.AKTORID }
            .firstOrNull { !it.isHistorisk }
            ?: throw ManglendeBrukerInfoException("Kunne ikke finne en gjeldende aktørId")
        return AktorId(gjeldendeAktorId.ident)
    }

    fun finnHistoriskeFoedselsnummer(): List<Foedselsnummer> =
        identer
            .filter { it.gruppe == Gruppe.FOLKEREGISTERIDENT && it.isHistorisk}
            .map { it.ident }
            .map { Foedselsnummer(it) }
}

class Ident(val ident: String, val isHistorisk: Boolean, val gruppe: Gruppe)

enum class Gruppe {
    FOLKEREGISTERIDENT, AKTORID, NPID
}