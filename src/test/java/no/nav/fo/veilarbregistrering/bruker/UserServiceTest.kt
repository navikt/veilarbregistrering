package no.nav.fo.veilarbregistrering.bruker

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.config.RequestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.servlet.http.HttpServletRequest

class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var pdlOppslagGateway: PdlOppslagGateway
    private lateinit var authContextHolder: AuthContextHolder

    @BeforeEach
    fun setup() {
        clearAllMocks()
        pdlOppslagGateway = mockk()
        authContextHolder = mockk()
        userService = UserService(pdlOppslagGateway, authContextHolder)
    }

    @Test
    fun skalHenteEnhetIdFraUrl() {
        val enhetId = "1234"
        val request: HttpServletRequest = mockk()
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns request
        every { request.getParameter("enhetId") } returns enhetId
        val enhetIdFraUrl = userService.enhetIdFromUrlOrThrow
        assertThat(enhetIdFraUrl).isEqualTo(enhetId)
    }

    @Test()
    fun skalFeileHvisUrlIkkeHarEnhetId() {
        val request: HttpServletRequest = mockk()
        every { request.getParameter("enhetId") } returns null
        assertThrows<RuntimeException> { userService.enhetIdFromUrlOrThrow }
    }

    @Test
    fun skalFinneBrukerGjennomPdl() {
        every { pdlOppslagGateway.hentIdenter(Foedselsnummer("11111111111")) } returns
            Identer(
                Arrays.asList(
                    Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                    Ident("22222222222", false, Gruppe.AKTORID),
                    Ident("33333333333", false, Gruppe.NPID)
                )
            )

        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer.of("11111111111"))
        assertThat(bruker.gjeldendeFoedselsnummer.stringValue()).isEqualTo("11111111111")
        assertThat(bruker.aktorId.aktorId).isEqualTo("22222222222")
    }
}