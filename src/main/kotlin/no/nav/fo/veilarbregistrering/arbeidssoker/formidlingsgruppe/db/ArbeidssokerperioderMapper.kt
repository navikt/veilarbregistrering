package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.db.Formidlingsgruppeendring
import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate

internal object ArbeidssokerperioderMapper {
    fun map(formidlingsgruppeendringer: List<Formidlingsgruppeendring>): Arbeidssokerperioder {
        return Arbeidssokerperioder(
                formidlingsgruppeendringer
                    .sortedByDescending { it.formidlingsgruppeEndret }
                    .filter(Formidlingsgruppeendring::erAktiv)
                    .run(::slettTekniskeISERVEndringer)
                    .run(::beholdKunSisteEndringPerDagIListen)
                    .run(::populerTilDatoMedNestePeriodesFraDatoMinusEn)
                    .run(::tilArbeidssokerperioder)
                    .sortedBy { it.periode.fra }
        )
    }

    private fun tilArbeidssokerperioder(formidlingsgruppeperioder: List<Formidlingsgruppeperiode>): List<Arbeidssokerperiode> {
        return formidlingsgruppeperioder
            .filter { it.formidlingsgruppe.erArbeidssoker() }
            .map { Arbeidssokerperiode(it.periode) }
    }

    private fun slettTekniskeISERVEndringer(formidlingsgruppeendringer: List<Formidlingsgruppeendring>) =
        formidlingsgruppeendringer.groupBy { it.formidlingsgruppeEndret }
            .values.flatMap { samtidigeEndringer -> if (samtidigeEndringer.size > 1) samtidigeEndringer.filter { !it.erISERV() } else samtidigeEndringer }
            .sortedByDescending { it.formidlingsgruppeEndret }

    private fun beholdKunSisteEndringPerDagIListen(formidlingsgruppeendringer: List<Formidlingsgruppeendring>): List<Formidlingsgruppeperiode> {
        val formidlingsgruppeperioder: MutableList<Formidlingsgruppeperiode> = mutableListOf()

        var forrigeEndretDato = LocalDate.MAX
        for (formidlingsgruppeendring in formidlingsgruppeendringer) {
            val endretDato = formidlingsgruppeendring.formidlingsgruppeEndret.toLocalDateTime().toLocalDate()
            if (endretDato.isEqual(forrigeEndretDato)) {
                continue
            }
            formidlingsgruppeperioder.add(
                Formidlingsgruppeperiode(
                    Formidlingsgruppe(formidlingsgruppeendring.formidlingsgruppe),
                    Periode(
                        endretDato,
                        null
                    )
                )
            )
            forrigeEndretDato = endretDato
        }

        return formidlingsgruppeperioder
    }

    private fun populerTilDatoMedNestePeriodesFraDatoMinusEn(formidlingsgruppeperioder: List<Formidlingsgruppeperiode>): List<Formidlingsgruppeperiode> =
        formidlingsgruppeperioder.mapIndexed { index, formidlingsgruppeperiode ->
            val forrigePeriodesFraDato = if (index > 0) formidlingsgruppeperioder[index - 1].periode.fra else null
            formidlingsgruppeperiode.tilOgMed(forrigePeriodesFraDato?.minusDays(1))
        }
}

internal data class Formidlingsgruppeperiode (val formidlingsgruppe: Formidlingsgruppe, val periode: Periode) {
    fun tilOgMed(tilDato: LocalDate?): Formidlingsgruppeperiode {
        return of(
            formidlingsgruppe,
            periode.tilOgMed(tilDato)
        )
    }

    companion object {
        fun of(formidlingsgruppe: Formidlingsgruppe, periode: Periode): Formidlingsgruppeperiode {
            return Formidlingsgruppeperiode(formidlingsgruppe, periode)
        }
    }
}