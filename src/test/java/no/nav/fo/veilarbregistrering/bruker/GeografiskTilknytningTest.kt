package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GeografiskTilknytningTest {

    @Test
    fun `fieldName skal bruke bydel ikke oslo hvis geografiskTilknytning er bydel men ikke oslo`() {
        assertThat(GeografiskTilknytning.of("123456").value())
            .isEqualTo("bydelIkkeOslo")
    }

    @Test
    fun `fieldName skal bruke bydel oslo med bydelskode hvis geografiskTilknytning er bydel i oslo`() {
        assertThat(GeografiskTilknytning.of("030106").value())
            .isEqualTo("bydelOsloUllern")
    }

    @Test
    fun `fieldName skal bruke fylke hvis geografiskTilknytning består av fire siffer`() {
        assertThat(GeografiskTilknytning.of("1234").value()).isEqualTo("kommune")
    }

    @Test
    fun `fieldName skal bruke utland hvis geografiskTilknytning består av tre bokstaver`() {
        assertThat(GeografiskTilknytning.of("ABC").value()).isEqualTo("utland")
    }

    @Test
    fun `exception skal kastes hvis geografiskTilknytning er ukjent`() {
        val illegalArgumentException = assertThrows<IllegalArgumentException> {
            GeografiskTilknytning.of("12").value()
        }
        assertThat(illegalArgumentException.message)
            .isEqualTo("Geografisk tilknytning har ukjent format: 12")
    }

    @Test
    fun `bydel bjerke i oslo er ikke by med bydel`() {
        val bjerke = GeografiskTilknytning.of(GeografiskTilknytning.BydelOslo.Bjerke.kode())
        assertThat(bjerke.byMedBydeler()).isFalse
    }

    @Test
    fun `oslo er by med bydeler`() {
        val oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Oslo.kode())
        assertThat(oslo.byMedBydeler()).isTrue
    }

    @Test
    fun `stavanger er by med bydeler`() {
        val oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Stavanger.kode())
        assertThat(oslo.byMedBydeler()).isTrue
    }

    @Test
    fun `bergen er by med bydeler`() {
        val oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Bergen.kode())
        assertThat(oslo.byMedBydeler()).isTrue
    }

    @Test
    fun `trondheim er by med bydeler`() {
        val oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Trondheim.kode())
        assertThat(oslo.byMedBydeler()).isTrue
    }
}
