package no.nav.fo.veilarbregistrering.profilering.resource

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.ProfilertInnsatsgruppeService
import no.nav.fo.veilarbregistrering.profilering.resources.ProfileringResource
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ProfileringResourceConfig::class])
class ProfileringResourceTest(@Autowired private val mvc: MockMvc) {
   @Test
   fun `get - Svarer med Pair`() {
       val responseBody = mvc.get("/api/profilering")
           .andExpect {
               status { isOk() }
           }.andReturn().response.contentAsString

       Assertions.assertThat(responseBody).isNotNull
   }
}

@Configuration
class ProfileringResourceConfig {

    @Bean
    fun profileringResource(profilertInnsatsgruppeService: ProfilertInnsatsgruppeService) : ProfileringResource {
        return ProfileringResource(mockk(relaxed = true), mockk(relaxed = true), profilertInnsatsgruppeService)
    }

    @Bean
    fun profilertInnsatsgruppeService(): ProfilertInnsatsgruppeService {
        val profilertInnsatsgruppeService = mockk<ProfilertInnsatsgruppeService>()

        every { profilertInnsatsgruppeService.hentProfilering(any()) } returns Pair(Innsatsgruppe.STANDARD_INNSATS, Servicegruppe("IVURD"))

        return profilertInnsatsgruppeService
    }
}
