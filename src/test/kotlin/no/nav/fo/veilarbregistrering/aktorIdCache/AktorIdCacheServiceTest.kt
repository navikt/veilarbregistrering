package no.nav.fo.veilarbregistrering.aktorIdCache

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.bruker.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AktorIdCacheServiceTest {

    private lateinit var aktorIdCacheService: AktorIdCacheService
    private val aktorIdCacheRepository: AktorIdCacheRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        every { aktorIdCacheRepository.hentAktørId(any()) } returns null

        val pdlOppslagGateway: PdlOppslagGateway = mockk(relaxed = true)
        every {
            pdlOppslagGateway.hentIdenter(
                any(),
                erSystemKontekst = true
            )
        } returns Identer(listOf(Ident(ident = "1234", isHistorisk = false, gruppe = Gruppe.AKTORID)))

        aktorIdCacheService = AktorIdCacheService(pdlOppslagGateway, aktorIdCacheRepository)
    }

    @Test
    fun `skal hente aktørId fra PDL og sette inn hvis den ikke finnes i cache`() {
        val fnr = Foedselsnummer("01234567890")

        aktorIdCacheService.hentAktorIdFraPDLHvisIkkeFinnes(fnr, true)

        verify(exactly = 1) { aktorIdCacheRepository.lagre(any()) }
    }
}