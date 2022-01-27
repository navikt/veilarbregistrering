package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.EldsteFoerst
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.arbeidssoker.Formidlingsgruppeendring.NyesteFoerst
import java.time.LocalDate

internal object ArbeidssokerperioderMapper {
    fun map(formidlingsgruppeendringer: List<Formidlingsgruppeendring>): Arbeidssokerperioder {
        return Arbeidssokerperioder(
                formidlingsgruppeendringer
                    .sortedWith(NyesteFoerst.nyesteFoerst())
                    .filter(Formidlingsgruppeendring::erAktiv)
                    .run(::slettTekniskeISERVEndringer)
                    .run(::beholdKunSisteEndringPerDagIListen)
                    .run(::populerTilDatoMedNestePeriodesFraDatoMinusEn)
                    .sortedWith(EldsteFoerst())
        )
    }

    private fun slettTekniskeISERVEndringer(formidlingsgruppeendringer: List<Formidlingsgruppeendring>) =
        formidlingsgruppeendringer.groupBy { it.formidlingsgruppeEndret }
            .values.flatMap { samtidigeEndringer -> if (samtidigeEndringer.size > 1) samtidigeEndringer.filter { !it.erISERV() } else samtidigeEndringer }
            .sortedWith(NyesteFoerst.nyesteFoerst())

    private fun beholdKunSisteEndringPerDagIListen(formidlingsgruppeendringer: List<Formidlingsgruppeendring>): List<Arbeidssokerperiode> {
        val arbeidssokerperioder: MutableList<Arbeidssokerperiode> = mutableListOf()

        var forrigeEndretDato = LocalDate.MAX
        for (formidlingsgruppeendring in formidlingsgruppeendringer) {
            val endretDato = formidlingsgruppeendring.formidlingsgruppeEndret.toLocalDateTime().toLocalDate()
            if (endretDato.isEqual(forrigeEndretDato)) {
                continue
            }
            arbeidssokerperioder.add(
                Arbeidssokerperiode(
                    Formidlingsgruppe.of(formidlingsgruppeendring.formidlingsgruppe),
                    Periode(
                        endretDato,
                        null
                    )
                )
            )
            forrigeEndretDato = endretDato
        }

        return arbeidssokerperioder
    }

    private fun populerTilDatoMedNestePeriodesFraDatoMinusEn(arbeidssokerperioder: List<Arbeidssokerperiode>): List<Arbeidssokerperiode> =
        arbeidssokerperioder.mapIndexed { index, arbeidssokerperiode ->
            val forrigePeriodesFraDato = if (index > 0) arbeidssokerperioder[index - 1].periode.fra else null
            arbeidssokerperiode.tilOgMed(forrigePeriodesFraDato?.minusDays(1))
        }
}