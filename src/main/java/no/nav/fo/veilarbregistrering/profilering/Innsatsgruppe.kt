package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.metrics.Metric
import java.util.*

/**
 * https://confluence.adeo.no/display/KARTLEGGING/Begrep+og+forkortelser
 */
enum class Innsatsgruppe(val arenakode: String) : Metric {
    STANDARD_INNSATS("IKVAL"),
    SITUASJONSBESTEMT_INNSATS("BFORM"),
    BEHOV_FOR_ARBEIDSEVNEVURDERING("BKART");

    override fun fieldName(): String {
        return "innsatsgruppe"
    }

    override fun value(): String {
        return this.toString()
    }

    companion object {
        fun of(arenakode: String): Innsatsgruppe {
            return Arrays.stream(values())
                    .filter { innsatsgruppe: Innsatsgruppe -> innsatsgruppe.arenakode == arenakode }
                    .findAny().orElse(null)
        }

        @JvmStatic
        fun of(besvarelse: Besvarelse, alder: Int, harJobbetSammenhengendeSeksAvTolvSisteManeder: Boolean): Innsatsgruppe {
            return if (besvarelse.anbefalerBehovForArbeidsevnevurdering()) {
                BEHOV_FOR_ARBEIDSEVNEVURDERING
            } else if (besvarelse.anbefalerStandardInnsats(alder, harJobbetSammenhengendeSeksAvTolvSisteManeder)) {
                STANDARD_INNSATS
            } else {
                SITUASJONSBESTEMT_INNSATS
            }
        }
    }
}