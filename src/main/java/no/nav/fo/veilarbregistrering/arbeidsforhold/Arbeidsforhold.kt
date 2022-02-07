package no.nav.fo.veilarbregistrering.arbeidsforhold

import java.time.LocalDate
import java.util.*

data class Arbeidsforhold(
    val arbeidsgiverOrgnummer: String?,
    val styrk: String = DEFAULT_STYRK,
    val fom: LocalDate?,
    val tom: LocalDate?,
    private val navArbeidsforholdId: String?
) {
    fun erDatoInnenforPeriode(innevaerendeMnd: LocalDate): Boolean {
        return innevaerendeMnd.isAfter(fom!!.minusDays(1)) &&
                (Objects.isNull(tom) || innevaerendeMnd.isBefore(tom!!.plusDays(1)))
    }

    fun organisasjonsnummer() =
        if (arbeidsgiverOrgnummer != null) Organisasjonsnummer(arbeidsgiverOrgnummer)
        else null

    override fun toString(): String {
        return "Arbeidsforhold(" +
                "arbeidsgiverOrgnummer=" + arbeidsgiverOrgnummer +
                ", styrk=" + styrk +
                ", fom=" + fom +
                ", tom=" + tom +
                ", navArbeidsforholdId=" + navArbeidsforholdId + ")"
    }

    companion object {
        @JvmStatic
        val DEFAULT_STYRK = "utenstyrkkode"

        @JvmStatic
        fun utenStyrkkode(): Arbeidsforhold {
            return Arbeidsforhold(null, DEFAULT_STYRK, null, null, null)
        }
    }
}