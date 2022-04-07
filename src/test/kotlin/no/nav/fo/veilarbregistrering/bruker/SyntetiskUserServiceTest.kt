package no.nav.fo.veilarbregistrering.bruker

import io.mockk.clearAllMocks
import io.mockk.mockk
import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.common.auth.context.AuthContextHolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SyntetiskUserServiceTest {

    private lateinit var userService: UserService
    private lateinit var pdlOppslagGateway: PdlOppslagGateway
    private lateinit var authContextHolder: AuthContextHolder

    @BeforeEach
    fun setup() {
        clearAllMocks()
        pdlOppslagGateway = mockk()
        authContextHolder = mockk()
        userService = UserService(pdlOppslagGateway, authContextHolder, true)
    }

    @AfterEach
    fun tearDown() {
        userService = UserService(pdlOppslagGateway, authContextHolder)
    }

    @ParameterizedTest
    @ValueSource(strings = ["63867500393", "01927397621", "05815598832", "03837197367", "03818197224"])
    fun `syntetiske foedselsnummer fra testfamilien skal funke hvis enablet`(input: String) {
        //syntetiske fødselsnummer enables ved å instansiere UserService m/ enableSyntetiskeFnr=true
        assertTrue(FodselsnummerValidator.isValid(input));
    }
}