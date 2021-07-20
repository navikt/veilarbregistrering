package no.nav.fo.veilarbregistrering.arbeidsforhold

import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class FlereArbeidsforholdTest {
    @Test
    fun `skal hente siste eller paagende arbeidsforhold`() {
        assertThat(flereArbeidsforholdTilfeldigSortert().siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.siste())
        val stigendeSortertListe = listOf(
            ArbeidsforholdTestdataBuilder.eldre(),
            ArbeidsforholdTestdataBuilder.nestSiste(),
            ArbeidsforholdTestdataBuilder.siste()
        )
        assertThat(FlereArbeidsforhold.of(stigendeSortertListe).siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.siste())
        val synkendeSortertListe = listOf(
            ArbeidsforholdTestdataBuilder.siste(),
            ArbeidsforholdTestdataBuilder.nestSiste(),
            ArbeidsforholdTestdataBuilder.eldre()
        )
        assertThat(FlereArbeidsforhold.of(synkendeSortertListe).siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.siste())

        // Skal hente paagendeArbeidsforhold
        val tilfeldigSortertListeMedPaagaaende = listOf(
            ArbeidsforholdTestdataBuilder.eldre(),
            ArbeidsforholdTestdataBuilder.paagaaende(),
            ArbeidsforholdTestdataBuilder.siste(),
            ArbeidsforholdTestdataBuilder.nestSiste()
        )
        assertThat(FlereArbeidsforhold.of(tilfeldigSortertListeMedPaagaaende).siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.paagaaende())
        val stigendeSortertListePaagaande = listOf(
            ArbeidsforholdTestdataBuilder.eldre(),
            ArbeidsforholdTestdataBuilder.nestSiste(),
            ArbeidsforholdTestdataBuilder.siste(),
            ArbeidsforholdTestdataBuilder.paagaaende()
        )
        assertThat(FlereArbeidsforhold.of(stigendeSortertListePaagaande).siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.paagaaende())
        val synkendeSortertListePaagaande = listOf(
            ArbeidsforholdTestdataBuilder.paagaaende(),
            ArbeidsforholdTestdataBuilder.siste(),
            ArbeidsforholdTestdataBuilder.nestSiste(),
            ArbeidsforholdTestdataBuilder.eldre()
        )
        assertThat(FlereArbeidsforhold.of(synkendeSortertListePaagaande).siste())
            .isEqualTo(ArbeidsforholdTestdataBuilder.paagaaende())
    }

    @Test
    fun `skal hente lengste av pågående arbeidsforhold`() {
        val fom3 = LocalDate.of(2017, 1, 1)
        val tom3: LocalDate? = null
        val fom2 = LocalDate.of(2017, 10, 1)
        val tom2: LocalDate? = null
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1: LocalDate? = null
        val paagaaendeArbeidsforholdVarighet3 = ArbeidsforholdTestdataBuilder.medDato(fom3, tom3)
        val paagaaendeArbeidsforholdVarighet2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val paagaaendeArbeidsforholdVarighet1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val flerePaagendeArbeidsforhold = listOf(
            paagaaendeArbeidsforholdVarighet2,
            paagaaendeArbeidsforholdVarighet1,
            paagaaendeArbeidsforholdVarighet3
        )
        assertThat(FlereArbeidsforhold.of(flerePaagendeArbeidsforhold).siste())
            .isEqualTo(paagaaendeArbeidsforholdVarighet3)
    }

    @Test
    fun `skal hente lengste av siste arbeidsforhold`() {
        val fom3 = LocalDate.of(2017, 1, 1)
        val tom3 = LocalDate.of(2017, 11, 30)
        val fom2 = LocalDate.of(2017, 10, 1)
        val tom2 = LocalDate.of(2017, 11, 30)
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1 = LocalDate.of(2017, 11, 30)
        val sisteArbeidsforholdVarighet3 = ArbeidsforholdTestdataBuilder.medDato(fom3, tom3)
        val sisteArbeidsforholdvarighet2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val sisteArbeidsforholdVarighet1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val flereSisteArbeidsforhold =
            listOf(sisteArbeidsforholdVarighet1, sisteArbeidsforholdvarighet2, sisteArbeidsforholdVarighet3)
        assertThat(FlereArbeidsforhold.of(flereSisteArbeidsforhold).siste())
            .isEqualTo(sisteArbeidsforholdVarighet3)
    }

    @Test
    fun `skal default arbeidsforhold`() {
        val arbeidsforhold = Arbeidsforhold(null, "utenstyrkkode", null, null, null)
        assertThat(FlereArbeidsforhold.of(emptyList()).siste()).isEqualTo(arbeidsforhold)
    }

    @Test
    fun `null arbeidsforhold skal håndteres som tom liste`() {
        assertThat(FlereArbeidsforhold.of(null).siste())
            .isEqualTo(FlereArbeidsforhold.of(emptyList()).siste())
    }

    @Test
    fun `skal ha arbeidsforhold på dato`() {
        val mnd = LocalDate.of(2017, 12, 1)
        val fom1 = LocalDate.of(2017, 10, 1)
        val tom1 = LocalDate.of(2017, 12, 1)
        val fom2 = LocalDate.of(2017, 12, 1)
        val tom2 = LocalDate.of(2017, 12, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harArbeidsforholdPaaDato(mnd)).isTrue
    }

    @Test
    fun `skal ikke ha arbeidsforhold på dato`() {
        val mnd = LocalDate.of(2018, 12, 1)
        val fom1 = LocalDate.of(2017, 10, 1)
        val tom1 = LocalDate.of(2017, 12, 1)
        val fom2 = LocalDate.of(2017, 12, 1)
        val tom2 = LocalDate.of(2017, 12, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harArbeidsforholdPaaDato(mnd)).isFalse
    }

    @Test
    fun `skal være i jobb 2 av 4 mnd`() {
        FlereArbeidsforhold.antallMnd = 4
        FlereArbeidsforhold.minAntallMndSammenhengendeJobb = 2
        val dagensDato = LocalDate.of(2017, 12, 20)
        val fom1 = LocalDate.of(2017, 10, 1)
        val tom1 = LocalDate.of(2017, 10, 31)
        val fom2 = LocalDate.of(2017, 9, 1)
        val tom2 = LocalDate.of(2017, 9, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(
            FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)
        ).isTrue
    }

    @Test
    fun `skal ikke være i jobb 2 av 4 mnd`() {
        FlereArbeidsforhold.antallMnd = 4
        FlereArbeidsforhold.minAntallMndSammenhengendeJobb = 2
        val dagensDato = LocalDate.of(2017, 12, 20)
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1 = LocalDate.of(2017, 11, 30)
        val fom2 = LocalDate.of(2017, 9, 1)
        val tom2 = LocalDate.of(2017, 9, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(
            FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)
        ).isFalse
    }

    @Test
    fun `to tomme arbeidsforhold skal være like`() {
        val arbeidsforholdFraSoap = FlereArbeidsforhold.of(null)
        val arbeidsforholdFraRest = FlereArbeidsforhold.of(null)
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isTrue
    }

    @Test
    fun `to lister med samme innhold skal være like`() {
        val arbeidsforholdFraSoap = flereArbeidsforholdTilfeldigSortert()
        val arbeidsforholdFraRest = flereArbeidsforholdTilfeldigSortert()
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isTrue
    }

    @Test
    fun `to lister med samme innhold men ulikt sortert skal være like`() {
        val arbeidsforholdFraSoap = FlereArbeidsforhold.of(
            listOf(
                ArbeidsforholdTestdataBuilder.eldre(),
                ArbeidsforholdTestdataBuilder.siste(),
                ArbeidsforholdTestdataBuilder.nestSiste()
            )
        )
        val arbeidsforholdFraRest = FlereArbeidsforhold.of(
            listOf(
                ArbeidsforholdTestdataBuilder.nestSiste(),
                ArbeidsforholdTestdataBuilder.eldre(),
                ArbeidsforholdTestdataBuilder.siste()
            )
        )
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isTrue
    }

    @Test
    fun `to ulike lister skal være ulike`() {
        val arbeidsforholdFraSoap = FlereArbeidsforhold.of(null)
        val arbeidsforholdFraRest = FlereArbeidsforhold.of(
            listOf(
                ArbeidsforholdTestdataBuilder.nestSiste(),
                ArbeidsforholdTestdataBuilder.eldre(),
                ArbeidsforholdTestdataBuilder.siste()
            )
        )
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isFalse
    }
}
