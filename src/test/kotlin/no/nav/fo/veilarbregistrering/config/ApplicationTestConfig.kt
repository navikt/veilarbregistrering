package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationTestConfig : ApplicationConfig() {
    final val pdlOppslagGateway: PdlOppslagGateway = mockk()
    final val authContextHolder: AuthContextHolder = mockk()

    @Bean
    fun userServiceStub(): UserService {
        return StubUserService()
    }

    @Bean
    override fun serviceToServiceTokenProvider(): ServiceToServiceTokenProvider = mockk()

    private inner class StubUserService : UserService(pdlOppslagGateway, authContextHolder) {
        override fun finnBrukerGjennomPdl(): Bruker = Bruker(aremark(), AktorId("232SA"))
    }
}
