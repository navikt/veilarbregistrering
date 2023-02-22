package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal object ArbeidssokerperioderMapper {

    fun filterBortIkkeAktivePersonIdOgTekniskeISERVEndringer(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): List<FormidlingsgruppeEndretEvent> {
        return formidlingsgruppeendringer
            .sortedByDescending { it.formidlingsgruppeEndret }
            .filter(FormidlingsgruppeEndretEvent::erAktiv)
            .run(::slettTekniskeISERVEndringer)
    }

    fun filtrerBortIkkeAktivPersonIdOgInitielleISERVInserts(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): List<FormidlingsgruppeEndretEvent> {
        val førOgEtterDatalast = formidlingsgruppeendringer.filter(FormidlingsgruppeEndretEvent::erAktiv)
            .partition { erInitiellDatalast(it) }
        val formidlingsgrupperFørDatalastVasket = slettInitielleISERVInsertsFraDatalast(førOgEtterDatalast.first)
        val formidlingsgrupperEtterDatalastVasket =
            slettInitielleISERVInsertsFraEtterDatalast(førOgEtterDatalast.second)
        val samledeResultater = formidlingsgrupperFørDatalastVasket + formidlingsgrupperEtterDatalastVasket
        return samledeResultater.sortedBy { it.formidlingsgruppeEndret }
    }

    fun map(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): Arbeidssokerperioder {
        return Arbeidssokerperioder(
                formidlingsgruppeendringer
                    .sortedByDescending { it.formidlingsgruppeEndret }
                    .filter(FormidlingsgruppeEndretEvent::erAktiv)
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

    private fun slettTekniskeISERVEndringer(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>) =
        formidlingsgruppeendringer.groupBy { it.formidlingsgruppeEndret }
            .values.flatMap { samtidigeEndringer -> if (samtidigeEndringer.size > 1) samtidigeEndringer.filter { !it.erISERV() } else samtidigeEndringer }
            .sortedByDescending { it.formidlingsgruppeEndret }

    /**
     Tror dette er en variant av "tekniske ISERV endringer": Når en person skal registreres som arbeidssøker og ikke allerede ligger i Arena,
     vil Arena gjøre en INSERT i formidlingsgruppe med formidlingsgruppe ISERV, også gjør de en UPDATE rett etter der status settes til ARBS/IARBS.
     Denne ISERV-verdien er ikke en "ekte" ISERV og kan forkastes.
     Merk at alle formidlingsgrupper fra initiell datalast har operasjon = INSERT. Derfor kan vi ikke filtrere bort ISERV-inserts før dette, for da fjerner vi også
     ekte ISERV-endringer.
     */
    private fun slettInitielleISERVInsertsFraEtterDatalast(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): List<FormidlingsgruppeEndretEvent> =
        formidlingsgruppeendringer.filter { it.operation != Operation.INSERT }

    /**
    Mulig denne kan brukes for alle innslag både før og etter datalast. Denne er veldig lik som `slettTekniskeISERVEndringer`,
     men basert på stikkprøver fra databasen, ser det ut som at det er noen sekunder forskjell på tidspunktet mellom initiell
     ISERV og påfølgende ARBS/IARBS som vi må hensynta når vi grupperer på tidspunkt.
     */
    private fun slettInitielleISERVInsertsFraDatalast(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): List<FormidlingsgruppeEndretEvent> =
        formidlingsgruppeendringer.groupBy { it.formidlingsgruppeEndret.truncatedTo(ChronoUnit.MINUTES) }.values
            .flatMap { samtidigeEndringer ->
                if (samtidigeEndringer.size > 1) samtidigeEndringer.filter { !it.erISERV() } else samtidigeEndringer
            }.sortedByDescending { it.formidlingsgruppeEndret }

    private fun erInitiellDatalast(formidlingsgruppeendring: FormidlingsgruppeEndretEvent) =
        // formidlingsgruppe_endret for den aller første update-meldingen er 2022-11-10 12:07:49, så vi kan regne med at alt før dette er en del av initiell last
        formidlingsgruppeendring.formidlingsgruppeEndret.isBefore(LocalDateTime.of(2022, 11, 10, 12, 7, 49))

    private fun beholdKunSisteEndringPerDagIListen(formidlingsgruppeendringer: List<FormidlingsgruppeEndretEvent>): List<Formidlingsgruppeperiode> {
        val formidlingsgruppeperioder: MutableList<Formidlingsgruppeperiode> = mutableListOf()

        var forrigeEndretDato = LocalDate.MAX
        for (formidlingsgruppeendring in formidlingsgruppeendringer) {
            val endretDato = formidlingsgruppeendring.formidlingsgruppeEndret.toLocalDate()
            if (endretDato.isEqual(forrigeEndretDato)) {
                continue
            }
            formidlingsgruppeperioder.add(
                Formidlingsgruppeperiode(
                    formidlingsgruppeendring.formidlingsgruppe,
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