package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationTestConfig : ApplicationConfig() {

    @Bean
    fun userServiceStub(): UserService {
        return StubUserService()
    }

    @Bean
    override fun systemUserTokenProvider(): SystemUserTokenProvider = mockk()

    @Bean
    override fun serviceToServiceTokenProvider(): ServiceToServiceTokenProvider = mockk()

    private inner class StubUserService : UserService(null, null) {
        override fun finnBrukerGjennomPdl(): Bruker = Bruker(aremark(), AktorId("232SA"))
    }
}
