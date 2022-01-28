package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Identer.Companion.of
import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IdenterTest {
    @Test
    fun `skal finne gjeldende fn`() {
        val identer = of(
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
        val identer = of(
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
        assertThrows<ManglendeBrukerInfoException> { of(ArrayList()).finnGjeldendeFnr() }
        assertThrows<ManglendeBrukerInfoException> { of(ArrayList()).finnGjeldendeAktorId() }
    }

    @Test
    fun `ingen gjeldende fnr skal gi notFound`() {
        val identer = of(
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
        val identer = of(
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
        val identer = of(
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
            Foedselsnummer.of("55555555555"),
            Foedselsnummer.of("44444444444")))
    }
}
