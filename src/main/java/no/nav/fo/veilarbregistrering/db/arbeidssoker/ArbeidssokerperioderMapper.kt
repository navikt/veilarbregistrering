package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode.EldsteFoerst
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.arbeidssoker.Formidlingsgruppeendring.NyesteFoerst
import java.time.LocalDate
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

internal object ArbeidssokerperioderMapper {
    @JvmStatic
    fun map(formidlingsgruppeendringer: List<Formidlingsgruppeendring>): Arbeidssokerperioder {
        return Arbeidssokerperioder(
                Optional.of(
                        formidlingsgruppeendringer.stream()
                                .sorted(NyesteFoerst.nyesteFoerst())
                                .collect(Collectors.toList()))
                        .map(beholdKunEndringerForAktiveIdenter)
                        .map(slettTekniskeISERVEndringer)
                        .map(beholdKunSisteEndringPerDagIListen)
                        .map(populerTilDato)
                        .get()
                        .stream()
                        .sorted(EldsteFoerst.eldsteFoerst())
                        .collect(Collectors.toList()))
    }

    private val beholdKunEndringerForAktiveIdenter = Function { formidlingsgruppeendringer: List<Formidlingsgruppeendring> ->
        formidlingsgruppeendringer.stream()
                .filter { obj: Formidlingsgruppeendring -> obj.erAktiv() }
                .collect(Collectors.toList())
    }
    private val slettTekniskeISERVEndringer = Function { formidlingsgruppeendringer: MutableList<Formidlingsgruppeendring> ->
        var j = 0
        while (j < formidlingsgruppeendringer.size - 1) {
            val endring = formidlingsgruppeendringer[j]
            val nesteEndring = formidlingsgruppeendringer[j + 1]
            if (erSamtidigeEndringer(endring, nesteEndring)) {
                if (endring.erISERV() && !nesteEndring.erISERV()) {
                    formidlingsgruppeendringer.removeAt(j)
                } else if (!endring.erISERV() && nesteEndring.erISERV()) {
                    formidlingsgruppeendringer.removeAt(j + 1)
                } else {
                    j++
                }
            } else {
                j++
            }
        }
        formidlingsgruppeendringer
    }

    private fun erSamtidigeEndringer(endring: Formidlingsgruppeendring, nesteEndring: Formidlingsgruppeendring): Boolean {
        return endring.formidlingsgruppeEndret.equals(nesteEndring.formidlingsgruppeEndret)
    }

    private val beholdKunSisteEndringPerDagIListen = Function<List<Formidlingsgruppeendring>, List<Arbeidssokerperiode>> { formidlingsgruppeendringer: List<Formidlingsgruppeendring> ->
        val arbeidssokerperioder: MutableList<Arbeidssokerperiode> = ArrayList(formidlingsgruppeendringer.size)
        var forrigeEndretDato: LocalDate? = null
        for (formidlingsgruppeendring in formidlingsgruppeendringer) {
            val endretDato = formidlingsgruppeendring.formidlingsgruppeEndret.toLocalDateTime().toLocalDate()
            if (forrigeEndretDato != null && endretDato.isEqual(forrigeEndretDato)) {
                continue
            }
            arbeidssokerperioder.add(Arbeidssokerperiode(
                    Formidlingsgruppe.of(formidlingsgruppeendring.formidlingsgruppe),
                    Periode.of(
                            endretDato,
                            null
                    )
            ))
            forrigeEndretDato = endretDato
        }
        arbeidssokerperioder
    }
    private val populerTilDato = Function<List<Arbeidssokerperiode>, List<Arbeidssokerperiode?>> { arbeidssokerperioder: List<Arbeidssokerperiode> ->
        val nyListe: MutableList<Arbeidssokerperiode?> = ArrayList<Arbeidssokerperiode?>(arbeidssokerperioder.size)
        var nyTildato: LocalDate? = null
        for (arbeidssokerperiode in arbeidssokerperioder) {
            nyListe.add(arbeidssokerperiode.tilOgMed(nyTildato))
            nyTildato = arbeidssokerperiode.periode.fra.minusDays(1)
        }
        nyListe
    }
}