package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.registrering.bruker.NavVeileder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class NavVeilederTest {
    val ident = "Z123456"
    val enhet = "Ustekveikja"
    val veileder1 = NavVeileder(ident, enhet)
    val veileder2 = NavVeileder(ident, enhet)

    @Test
    fun `to veilederobjekter med samme data skal vare like ihht equals`() {
        assertTrue(veileder1 ==  veileder2)
    }

    @Test
    fun `To veilederobjekter med samme data har samme hashcode`() {
        assertEquals(veileder1.hashCode(), veileder2.hashCode())
    }

    @Test
    fun `To veilederobjekter med ulik data har ulik hashcode`() {
        assertNotEquals(veileder1.hashCode(), NavVeileder("Z001001", "NAV Oslo"))
    }
}