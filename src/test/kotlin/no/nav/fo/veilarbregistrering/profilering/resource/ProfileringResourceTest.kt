package no.nav.fo.veilarbregistrering.profilering.resource

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.ProfilertInnsatsgruppeService
import no.nav.fo.veilarbregistrering.profilering.resources.ProfileringResource
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.assertEquals

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ProfileringResourceConfig::class])
class ProfileringResourceTest(@Autowired private val mvc: MockMvc) {
   @Test
   fun `get profilering returnerer Pair av innsatsgruppe og servicegruppe`() {
       val responseBody = mvc.get("/api/profilering")
           .andExpect {
               status { isOk() }
           }.andReturn().response.contentAsString

       System.out.println(responseBody)
       assertEquals("{\"innsatsgruppe\":\"STANDARD_INNSATS\",\"servicegruppe\":\"IVURD\"}", responseBody)
   }

    @Test
    fun `get standard-innsats returnerer boolsk verdi`() {
        val responseBody = mvc.get("/api/profilering/standard-innsats")
            .andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

        assertEquals("false", responseBody)
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
        every { profilertInnsatsgruppeService.erStandardInnsats(any()) } returns false

        return profilertInnsatsgruppeService
    }
}
