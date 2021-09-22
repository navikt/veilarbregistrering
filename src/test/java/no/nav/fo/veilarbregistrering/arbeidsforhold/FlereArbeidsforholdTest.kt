package no.nav.fo.veilarbregistrering.arbeidsforhold

import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class FlereArbeidsforholdTest {
    @Test
    fun `skal hente siste eller paagende arbeidsforhold`() {
        assertThat(flereArbeidsforholdTilfeldigSortert().siste())
            .isEqualTo(ArbeidsforholdTestdata.siste())
        val stigendeSortertListe = listOf(
            ArbeidsforholdTestdata.eldre(),
            ArbeidsforholdTestdata.nestSiste(),
            ArbeidsforholdTestdata.siste()
        )
        assertThat(FlereArbeidsforhold.of(stigendeSortertListe).siste())
            .isEqualTo(ArbeidsforholdTestdata.siste())
        val synkendeSortertListe = listOf(
            ArbeidsforholdTestdata.siste(),
            ArbeidsforholdTestdata.nestSiste(),
            ArbeidsforholdTestdata.eldre()
        )
        assertThat(FlereArbeidsforhold.of(synkendeSortertListe).siste())
            .isEqualTo(ArbeidsforholdTestdata.siste())

        // Skal hente paagendeArbeidsforhold
        val tilfeldigSortertListeMedPaagaaende = listOf(
            ArbeidsforholdTestdata.eldre(),
            ArbeidsforholdTestdata.paagaaende(),
            ArbeidsforholdTestdata.siste(),
            ArbeidsforholdTestdata.nestSiste()
        )
        assertThat(FlereArbeidsforhold.of(tilfeldigSortertListeMedPaagaaende).siste())
            .isEqualTo(ArbeidsforholdTestdata.paagaaende())
        val stigendeSortertListePaagaande = listOf(
            ArbeidsforholdTestdata.eldre(),
            ArbeidsforholdTestdata.nestSiste(),
            ArbeidsforholdTestdata.siste(),
            ArbeidsforholdTestdata.paagaaende()
        )
        assertThat(FlereArbeidsforhold.of(stigendeSortertListePaagaande).siste())
            .isEqualTo(ArbeidsforholdTestdata.paagaaende())
        val synkendeSortertListePaagaande = listOf(
            ArbeidsforholdTestdata.paagaaende(),
            ArbeidsforholdTestdata.siste(),
            ArbeidsforholdTestdata.nestSiste(),
            ArbeidsforholdTestdata.eldre()
        )
        assertThat(FlereArbeidsforhold.of(synkendeSortertListePaagaande).siste())
            .isEqualTo(ArbeidsforholdTestdata.paagaaende())
    }

    @Test
    fun `skal hente lengste av pågående arbeidsforhold`() {
        val fom3 = LocalDate.of(2017, 1, 1)
        val tom3: LocalDate? = null
        val fom2 = LocalDate.of(2017, 10, 1)
        val tom2: LocalDate? = null
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1: LocalDate? = null
        val paagaaendeArbeidsforholdVarighet3 = ArbeidsforholdTestdata.medDato(fom3, tom3)
        val paagaaendeArbeidsforholdVarighet2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
        val paagaaendeArbeidsforholdVarighet1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
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
        val sisteArbeidsforholdVarighet3 = ArbeidsforholdTestdata.medDato(fom3, tom3)
        val sisteArbeidsforholdvarighet2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
        val sisteArbeidsforholdVarighet1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
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
        val arbeidsforhold1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
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
        val arbeidsforhold1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harArbeidsforholdPaaDato(mnd)).isFalse
    }

    @Test
    fun `skal være i jobb 2 av 4 mnd`() {
        val antallMnd = 4
        val minAntallMndSammenhengendeJobb = 2
        val dagensDato = LocalDate.of(2017, 12, 20)
        val fom1 = LocalDate.of(2017, 10, 1)
        val tom1 = LocalDate.of(2017, 10, 31)
        val fom2 = LocalDate.of(2017, 9, 1)
        val tom2 = LocalDate.of(2017, 9, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(
            FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSisteManeder(dagensDato, minAntallMndSammenhengendeJobb, antallMnd)
        ).isTrue
    }

    @Test
    fun `skal ikke være i jobb 2 av 4 mnd`() {
        val antallMnd = 4
        val minAntallMndSammenhengendeJobb = 2
        val dagensDato = LocalDate.of(2017, 12, 20)
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1 = LocalDate.of(2017, 11, 30)
        val fom2 = LocalDate.of(2017, 9, 1)
        val tom2 = LocalDate.of(2017, 9, 30)
        val arbeidsforhold1 = ArbeidsforholdTestdata.medDato(fom1, tom1)
        val arbeidsforhold2 = ArbeidsforholdTestdata.medDato(fom2, tom2)
        val arbeidsforhold = listOf(arbeidsforhold1, arbeidsforhold2)
        assertThat(
            FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSisteManeder(dagensDato, minAntallMndSammenhengendeJobb, antallMnd)
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
                ArbeidsforholdTestdata.eldre(),
                ArbeidsforholdTestdata.siste(),
                ArbeidsforholdTestdata.nestSiste()
            )
        )
        val arbeidsforholdFraRest = FlereArbeidsforhold.of(
            listOf(
                ArbeidsforholdTestdata.nestSiste(),
                ArbeidsforholdTestdata.eldre(),
                ArbeidsforholdTestdata.siste()
            )
        )
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isTrue
    }

    @Test
    fun `to ulike lister skal være ulike`() {
        val arbeidsforholdFraSoap = FlereArbeidsforhold.of(null)
        val arbeidsforholdFraRest = FlereArbeidsforhold.of(
            listOf(
                ArbeidsforholdTestdata.nestSiste(),
                ArbeidsforholdTestdata.eldre(),
                ArbeidsforholdTestdata.siste()
            )
        )
        assertThat(arbeidsforholdFraSoap.erLik(arbeidsforholdFraRest)).isFalse
    }
}
