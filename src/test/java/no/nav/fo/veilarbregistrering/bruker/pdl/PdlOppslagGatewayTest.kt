package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.bruker.AktorId
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.junit.jupiter.api.AfterEach
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlFoedsel
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlTelefonnummer
import no.nav.fo.veilarbregistrering.config.CacheConfig
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class PdlOppslagGatewayTest {

    private lateinit var pdlOppslagClient: PdlOppslagClient
    private lateinit var context: AnnotationConfigApplicationContext

    @BeforeEach
    fun setup() {
        pdlOppslagClient = mockk()
        val beanDefinition: BeanDefinition = BeanDefinitionBuilder
            .rootBeanDefinition(PdlOppslagGatewayImpl::class.java)
            .addConstructorArgValue(pdlOppslagClient)
            .beanDefinition
        context = AnnotationConfigApplicationContext()
        context.register(CacheConfig::class.java, CacheAutoConfiguration::class.java)
        context.defaultListableBeanFactory.registerBeanDefinition("pdlOppslagClient", beanDefinition)
        context.refresh()
        context.start()
    }

    @AfterEach
    fun tearDown() = context.stop()

    @Test
    fun `skal cache ved kall på samme fnr`() {
        val pdlOppslagGateway = context.getBean(PdlOppslagGateway::class.java)
        every { pdlOppslagClient.hentIdenter(any<Foedselsnummer>())} returns dummyPdlIdent()

        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("22222222222"))
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("22222222222"))

        verify(exactly = 1) { pdlOppslagClient.hentIdenter(any<Foedselsnummer>()) }
    }

    @Test
    fun `skal ikke cache ved kall på forskjellig fnr`() {
        val pdlOppslagGateway = context.getBean(PdlOppslagGateway::class.java)
        every { pdlOppslagClient.hentIdenter(any<Foedselsnummer>())} returns dummyPdlIdent()

        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("12345678910"))
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("109987654321"))

        verify(exactly = 2) { pdlOppslagClient.hentIdenter(any<Foedselsnummer>()) }
    }

    private fun dummyPdlIdent(): PdlIdenter {
        val pdlIdent = PdlIdent(ident = "12345678910", historisk = false, gruppe = PdlGruppe.FOLKEREGISTERIDENT)
        return PdlIdenter(listOf(pdlIdent))
    }

    @Test
    fun `skal cache ved kall på samme aktor id`() {
        val pdlOppslagGateway = context.getBean(PdlOppslagGateway::class.java)
        every { pdlOppslagClient.hentPerson(any<AktorId>())} returns dummyPdlPerson()

        pdlOppslagGateway.hentPerson(AktorId.of("22222222222"))
        pdlOppslagGateway.hentPerson(AktorId.of("22222222222"))

        verify(exactly = 1) { pdlOppslagClient.hentPerson(any<AktorId>()) }
    }

    @Test
    fun `skal ikke cache ved kall på forskjellig aktor id`() {
        val pdlOppslagGateway = context.getBean(PdlOppslagGateway::class.java)
        every { pdlOppslagClient.hentPerson(any<AktorId>())} returns dummyPdlPerson()

        pdlOppslagGateway.hentPerson(AktorId.of("12345678910"))
        pdlOppslagGateway.hentPerson(AktorId.of("109987654321"))

        verify(exactly = 2) { pdlOppslagClient.hentPerson(any<AktorId>()) }
    }

    private fun dummyPdlPerson(): PdlPerson {
        val pdlFoedsel = PdlFoedsel(LocalDate.of(1970, 3, 23))
        val pdlTelefonnummer = PdlTelefonnummer("94242425", "0047", 0)
        return PdlPerson(listOf(pdlTelefonnummer), listOf(pdlFoedsel), emptyList())
    }
}
