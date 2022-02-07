package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IdenterTest {
    @Test
    fun `skal finne gjeldende fn`() {
        val identer = Identer(
            listOf(
                Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                Ident("44444444444", true, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID),
                Ident("33333333333", false, Gruppe.NPID)
            )
        )
        val fnr = identer.finnGjeldendeFnr()
        assertThat(fnr.stringValue()).isEqualTo("11111111111")
    }

    @Test
    fun `skal finne gjeldende aktorid`() {
        val identer = Identer(
             listOf(
                Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID),
                Ident("44444444444", true, Gruppe.AKTORID),
                Ident("33333333333", false, Gruppe.NPID)
            )
        )
        val aktorId = identer.finnGjeldendeAktorId()
        assertThat(aktorId.aktorId).isEqualTo("22222222222")
    }

    @Test
    fun `tom liste skal gi notFound`() {
        assertThrows<ManglendeBrukerInfoException> { Identer(ArrayList()).finnGjeldendeFnr() }
        assertThrows<ManglendeBrukerInfoException> { Identer(ArrayList()).finnGjeldendeAktorId() }
    }

    @Test
    fun `ingen gjeldende fnr skal gi notFound`() {
        val identer = Identer(
             listOf(
                Ident("11111111111", true, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID),
                Ident("33333333333", false, Gruppe.NPID)
            )
        )
        assertThrows<ManglendeBrukerInfoException> { identer.finnGjeldendeFnr() }
    }

    @Test
    fun `ingen gjeldende aktorid skal gi notFound`() {
        val identer = Identer(
             listOf(
                Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", true, Gruppe.AKTORID),
                Ident("33333333333", false, Gruppe.NPID)
            )
        )
        assertThrows<ManglendeBrukerInfoException> { identer.finnGjeldendeAktorId() }
    }

    @Test
    fun `skal finne historiske fnr`() {
        val identer = Identer(
             listOf(
                Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                Ident("44444444444", true, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", true, Gruppe.AKTORID),
                Ident("33333333333", false, Gruppe.NPID),
                Ident("55555555555", true, Gruppe.FOLKEREGISTERIDENT)
            )
        )
        val historiskeFnr = identer.finnHistoriskeFoedselsnummer()
        assertThat(historiskeFnr.size).isEqualTo(2)
        assertThat(historiskeFnr).containsAll(listOf(
            Foedselsnummer("55555555555"),
            Foedselsnummer("44444444444")))
    }
}
