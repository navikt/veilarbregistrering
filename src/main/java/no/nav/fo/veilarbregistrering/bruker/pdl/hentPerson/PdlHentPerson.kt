package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlError
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse
import java.time.LocalDate
import java.util.*

data class PdlHentPerson(val hentPerson: PdlPerson)


data class PdlPerson(
    val telefonnummer: List<PdlTelefonnummer>,
    val foedsel: List<PdlFoedsel>,
    val adressebeskyttelse: List<PdlAdressebeskyttelse>) {

    fun hoyestPrioriterteTelefonnummer() =
        (if (telefonnummer.isEmpty()) Optional.empty()
        else telefonnummer.stream()
            .sorted()
            .findFirst())!!

    fun getSistePdlFoedsel() =
        if (foedsel.isEmpty()) Optional.empty()
        else Optional.of(foedsel[foedsel.size - 1])

    fun strengesteAdressebeskyttelse() =
        (if (adressebeskyttelse == null || adressebeskyttelse.isEmpty()) Optional.empty()
        else adressebeskyttelse.stream()
            .sorted()
            .findFirst())!!
}

data class PdlTelefonnummer(val nummer: String? = null,
                            val landskode: String? = null,
                            val prioritet: Int = 0) : Comparable<PdlTelefonnummer> {

    override operator fun compareTo(o: PdlTelefonnummer): Int {
        if (prioritet > o.prioritet) {
            return 1
        }
        return if (prioritet < o.prioritet) {
            -1
        } else 0
    }
}

data class PdlFoedsel(val foedselsdato: LocalDate)

enum class PdlGradering(internal val niva: Int) {
    STRENGT_FORTROLIG_UTLAND(3),  // Tilsvarer paragraf 19 i Bisys (henvisning til Forvaltningslovens §19)
    STRENGT_FORTROLIG(2),  // Tidligere spesregkode kode 6 fra TPS
    FORTROLIG(1),  // Tidligere spesregkode kode 7 fra TPS
    UGRADERT(0);
}

data class PdlAdressebeskyttelse(val gradering: PdlGradering) : Comparable<PdlAdressebeskyttelse> {
    override operator fun compareTo(other: PdlAdressebeskyttelse) = other.gradering.niva - gradering.niva
}


data class PdlHentPersonRequest(val query: String, val variables: HentPersonVariables)

data class PdlHentPersonResponse(val data: PdlHentPerson, private val errors: MutableList<PdlError>) : PdlResponse {
    override fun getErrors() = errors
}

data class HentPersonVariables(val ident: String, val oppholdHistorikk: Boolean)

