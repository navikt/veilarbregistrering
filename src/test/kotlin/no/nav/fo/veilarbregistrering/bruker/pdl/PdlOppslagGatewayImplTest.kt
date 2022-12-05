package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlIdentBolk
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlIdenterBolk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PdlOppslagGatewayImplTest {

    @Test
    fun `skal returnere alle vellykkede identer`() {
        val pdlOppslagClient = mockk<PdlOppslagClient>(relaxed = true)
        every { pdlOppslagClient.hentIdenterBolk(any()) } returns listOf(
            PdlIdenterBolk("2057713142949", listOf(PdlIdentBolk(aremark().foedselsnummer)), "ok"),
            PdlIdenterBolk("2877772301361", listOf(PdlIdentBolk("12345678911")), "ok")
        )

        val pdlOppslagGatewayImpl = PdlOppslagGatewayImpl(pdlOppslagClient)

        val identerBolk =
            pdlOppslagGatewayImpl.hentIdenterBolk(listOf(AktorId("2057713142949"), AktorId("2877772301361")))

        assertThat(identerBolk).hasSize(2)
    }

    @Test
    fun `skal filtrere bort identer som fikk feilkode fra PDL`() {
        val pdlOppslagClient = mockk<PdlOppslagClient>(relaxed = true)
        every { pdlOppslagClient.hentIdenterBolk(any()) } returns listOf(
            PdlIdenterBolk("2057713142949", listOf(PdlIdentBolk(aremark().foedselsnummer)), "ok"),
            PdlIdenterBolk("2877772301361", null, "not_found")
        )

        val pdlOppslagGatewayImpl = PdlOppslagGatewayImpl(pdlOppslagClient)

        val identerBolk =
            pdlOppslagGatewayImpl.hentIdenterBolk(listOf(AktorId("2057713142949"), AktorId("2877772301361")))

        assertThat(identerBolk).hasSize(1)
    }
}