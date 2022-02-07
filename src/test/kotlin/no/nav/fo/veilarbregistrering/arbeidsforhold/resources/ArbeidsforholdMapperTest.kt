package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidsforholdMapperTest {

    @Test
    fun `skal mappe alle verdier`() {
        val fom = LocalDate.of(2017, 12, 1)
        val tom = LocalDate.of(2017, 12, 30)
        val arbeidsforhold = Arbeidsforhold("453542352", "STK", fom, tom, null)
        val arbeidsforholdDto = ArbeidsforholdDto.fra(arbeidsforhold)
        val softAssertions = SoftAssertions()
        softAssertions.assertThat(arbeidsforholdDto.arbeidsgiverOrgnummer).isEqualTo("453542352")
        softAssertions.assertThat(arbeidsforholdDto.styrk).isEqualTo("STK")
        softAssertions.assertThat(arbeidsforholdDto.fom).isEqualTo(fom)
        softAssertions.assertThat(arbeidsforholdDto.tom).isEqualTo(tom)
        softAssertions.assertAll()
    }
}
