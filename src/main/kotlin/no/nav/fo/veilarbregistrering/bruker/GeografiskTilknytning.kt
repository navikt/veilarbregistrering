package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.metrics.Metric
import java.util.*
import java.util.stream.Collectors

/**
 * Geografisk tilknytning kan være 1 av 3:
 *
 *  * Landkode (3 bokstaver)
 *  * Kommune (4 siffer)
 *  * Bydel (6 siffer)
 *
 */
data class GeografiskTilknytning(private val geografisktilknytning: String) : Metric {

    fun stringValue(): String {
        return geografisktilknytning
    }

    override fun toString(): String {
        return "Geografisk tilknytning[$geografisktilknytning]"
    }

    override fun fieldName(): String {
        return "geografiskTilknytning"
    }

    override fun value(): String {
        val fieldName: String
        fieldName = if (utland()) {
            "utland"
        } else if (kommune()) {
            "kommune"
        } else if (bydelIkkeOslo()) {
            "bydelIkkeOslo"
        } else if (bydelOslo()) {
            "bydelOslo" + BydelOslo.of(geografisktilknytning).name
        } else {
            throw IllegalArgumentException("Geografisk tilknytning har ukjent format: $geografisktilknytning")
        }
        return fieldName
    }

    fun utland(): Boolean {
        return geografisktilknytning.length == 3 && geografisktilknytning.matches(Regex("^[a-åA-Å]*$"))
    }

    private fun kommune(): Boolean {
        return geografisktilknytning.length == 4 && geografisktilknytning.matches(Regex("^[0-9]*$"))
    }

    private fun bydelOslo(): Boolean {
        return geografisktilknytning.length == 6 && BydelOslo.contains(geografisktilknytning)
    }

    private fun bydelIkkeOslo(): Boolean {
        return geografisktilknytning.length == 6 && !BydelOslo.contains(geografisktilknytning)
    }

    fun byMedBydeler(): Boolean {
        return ByMedBydeler.byMedBydelerAsKode().contains(geografisktilknytning)
    }

    companion object {
        fun ukjentBostedsadresse(): GeografiskTilknytning {
            return GeografiskTilknytning("NOR")
        }
    }

    internal enum class ByMedBydeler(private val kode: String) {
        Oslo("0301"), Stavanger("1103"), Bergen("4601"), Trondheim("5001");

        fun kode(): String {
            return kode
        }

        companion object {
            fun byMedBydelerAsKode(): List<String> {
                return Arrays.stream(values())
                    .map { by: ByMedBydeler -> by.kode }
                    .collect(Collectors.toList())
            }
        }
    }

    internal enum class BydelOslo(private val kode: String, private val navn: String) {
        GamleOslo("030101", "Gamle Oslo"), Grunerlokka("030102", "Grünerløkka"), Sagene(
            "030103",
            "Sagene"
        ),
        StHanshaugen("030104", "St.Hanshaugen"), Frogner("030105", "Frogner"), Ullern(
            "030106",
            "Ullern"
        ),
        VestreAker("030107", "Vestre Aker"), NordreAker("030108", "Nordre Aker"), Bjerke(
            "030109",
            "Bjerke"
        ),
        Grorud("030110", "Grorud"), Stovner("030111", "Stovner"), Alna("030112", "Alna"), Ostensjo(
            "030113",
            "Østensjø"
        ),
        Nordstrand("030114", "Nordstrand"), SondreNordstrand("030115", "Søndre Nordstrand"), Sentrum(
            "030116", "Sentrum"
        ),
        Marka("030117", "Marka");

        fun kode(): String {
            return kode
        }

        fun navn(): String {
            return navn
        }

        companion object {
            internal fun of(geografisktilknytning: String): BydelOslo {
                return Arrays.stream(values())
                    .filter { bydelOslo: BydelOslo -> bydelOslo.kode == geografisktilknytning }
                    .findFirst()
                    .orElseThrow { IllegalStateException("$geografisktilknytning er ikke en kjent kode for noen bydel i Oslo.") }
            }

            internal operator fun contains(geografisktilknytning: String): Boolean {
                return Arrays.stream(values())
                    .anyMatch { bydelOslo: BydelOslo -> bydelOslo.kode == geografisktilknytning }
            }
        }
    }
}