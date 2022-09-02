package no.nav.fo.veilarbregistrering.bruker

import io.mockk.*
import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.config.RequestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
    fun skalHenteFnrFraTokenForPersonbruker() {
        mockkStatic("no.nav.fo.veilarbregistrering.bruker.UserServiceKt")
        val request: HttpServletRequest = mockk()
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns request
        every { authContextHolder.erEksternBruker() } returns true
        every { authContextHolder.hentFnrFraPid() } returns aremark().stringValue()
        every { pdlOppslagGateway.hentIdenter(aremark()) } returns
                Identer(
                    listOf(
                        Ident(aremark().stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                        Ident("22222222222", false, Gruppe.AKTORID)
                    )
                )

        userService.finnBrukerGjennomPdl()

        verify { request wasNot called }
    }

    @Test
    fun skalHenteFnrFraUrlForVeilederOgSystembruker() {
        mockkStatic("no.nav.fo.veilarbregistrering.bruker.UserServiceKt")
        val request: HttpServletRequest = mockk()
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns request
        every { request.getParameter("fnr") } returns aremark().stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(aremark()) } returns
                Identer(
                    listOf(
                        Ident(aremark().stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                        Ident("22222222222", false, Gruppe.AKTORID)
                    )
                )

        userService.finnBrukerGjennomPdl()

        verify(exactly = 0) { authContextHolder.hentFnrFraPid() }
    }

    @Test
    fun skalHenteEnhetIdFraUrl() {
        val enhetId = "1234"
        val request: HttpServletRequest = mockk()
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns request
        every { request.getParameter("enhetId") } returns enhetId
        val enhetIdFraUrl = userService.getEnhetIdFromUrlOrThrow()
        assertThat(enhetIdFraUrl).isEqualTo(enhetId)
    }

    @Test
    fun skalFeileHvisUrlIkkeHarEnhetId() {
        val request: HttpServletRequest = mockk()
        every { request.getParameter("enhetId") } returns null
        assertThrows<RuntimeException> { userService.getEnhetIdFromUrlOrThrow() }
    }

    @Test
    fun skalFinneBrukerGjennomPdl() {
        every { pdlOppslagGateway.hentIdenter(Foedselsnummer("11111111111")) } returns
            Identer(
                listOf(
                    Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                    Ident("22222222222", false, Gruppe.AKTORID),
                    Ident("33333333333", false, Gruppe.NPID)
                )
            )

        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer("11111111111"))
        assertThat(bruker.gjeldendeFoedselsnummer.stringValue()).isEqualTo("11111111111")
        assertThat(bruker.aktorId.aktorId).isEqualTo("22222222222")
    }

    @ParameterizedTest
    @ValueSource(strings = ["63867500393", "01927397621", "05815598832", "03837197367", "03818197224"])
    fun `syntetiske foedselsnummer fra testfamilien skal IKKE funke som default`(input: String) {
        //syntetiske fødselsnummer enables ved å instansiere UserService m/ enableSyntetiskeFnr=true
        assertFalse(FodselsnummerValidator.isValid(input))
    }
}