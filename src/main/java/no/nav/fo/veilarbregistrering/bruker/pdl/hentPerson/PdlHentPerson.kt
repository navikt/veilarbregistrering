package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson

import java.time.LocalDate
import java.util.*

data class PdlHentPerson(val hentPerson: PdlPerson)


data class PdlPerson(
    val telefonnummer: List<PdlTelefonnummer>,
    val foedsel: List<PdlFoedsel>,
    val adressebeskyttelse: List<PdlAdressebeskyttelse>) {

    fun hoyestPrioriterteTelefonnummer() =
        if (telefonnummer!!.isEmpty()) Optional.empty()
        else telefonnummer!!.stream()
            .sorted()
            .findFirst()

    fun getSistePdlFoedsel() =
        if (foedsel!!.isEmpty()) Optional.empty()
        else Optional.of(foedsel!![foedsel!!.size - 1])

    fun strengesteAdressebeskyttelse() =
        if (adressebeskyttelse == null || adressebeskyttelse!!.isEmpty()) Optional.empty()
        else adressebeskyttelse!!.stream()
            .sorted()
            .findFirst()
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

