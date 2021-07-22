package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlTelefonnummer
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlPerson
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlAdressebeskyttelse
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGradering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PdlPersonTest {

    @Test
    fun `hoyestPrioriterteTelefonnummer skal returneres`() {
        val pdlTelefonnummer1 = PdlTelefonnummer(prioritet = 1)
        val pdlTelefonnummer2 = PdlTelefonnummer(prioritet = 2)
        val pdlTelefonnummer3 = PdlTelefonnummer(prioritet = 3)
        val telefonnummer = listOf(pdlTelefonnummer2, pdlTelefonnummer3, pdlTelefonnummer1)
        val pdlPerson = PdlPerson(telefonnummer, emptyList(), emptyList())

        assertThat(pdlPerson.hoyestPrioriterteTelefonnummer()).hasValue(pdlTelefonnummer1)
    }

    @Test
    fun `strengesteAdressebeskyttelse uten eksplisitt graderingsniva`() {
        assertThat(personMedAdressebeskyttelse().strengesteAdressebeskyttelse()).isEmpty
    }

    @Test
    fun `strengesteAdressebeskyttelse med en gradering`() {
        for (gradering: PdlGradering in PdlGradering.values()) {
            val enkeltgradertPerson = personMedAdressebeskyttelse((gradering))
            val strengesteGradering = strengesteGraderingForPerson(enkeltgradertPerson)
            assertThat(strengesteGradering).isEqualTo(gradering)
        }
    }

    @Test
    fun `strengesteAdressebeskyttelse med flere graderinger`() {
        assertThat(
            strengesteGraderingForPerson(
                personMedAdressebeskyttelse(PdlGradering.UGRADERT, PdlGradering.FORTROLIG)
            )
        ).isEqualTo(PdlGradering.FORTROLIG)
        assertThat(
            strengesteGraderingForPerson(
                personMedAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG, PdlGradering.FORTROLIG)
            )
        ).isEqualTo(PdlGradering.STRENGT_FORTROLIG)
        assertThat(
            strengesteGraderingForPerson(
                personMedAdressebeskyttelse(
                    PdlGradering.STRENGT_FORTROLIG,
                    PdlGradering.STRENGT_FORTROLIG_UTLAND,
                    PdlGradering.FORTROLIG
                )
            )
        ).isEqualTo(PdlGradering.STRENGT_FORTROLIG_UTLAND)
    }

    private fun strengesteGraderingForPerson(person: PdlPerson): PdlGradering {
        return person.strengesteAdressebeskyttelse()
            .map(PdlAdressebeskyttelse::gradering).orElse(null)
    }

    private fun personMedAdressebeskyttelse(vararg pdlGraderinger: PdlGradering): PdlPerson {
        val graderinger = listOf(*pdlGraderinger).map { PdlAdressebeskyttelse(it) }
        return PdlPerson(emptyList(), emptyList(), graderinger)
    }
}
