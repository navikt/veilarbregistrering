import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlAdressebeskyttelse
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlGradering
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class KotlinThingTest {

    @Test
    fun `Tester noe fra Kotlin`() {
        val person = PdlPerson()
        person.adressebeskyttelse = listOf(PdlAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG))
        assertThat(person.strengesteAdressebeskyttelse().get().gradering).isEqualTo(PdlGradering.STRENGT_FORTROLIG)
    }
}