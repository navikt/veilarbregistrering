package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import no.nav.fo.veilarbregistrering.config.CacheConfig
import org.junit.jupiter.api.Test

internal class HentIdenterPdlOppslagGatewayTest {

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
        val pdlIdent = PdlIdent()
        pdlIdent.ident = "12345678910"
        pdlIdent.isHistorisk = false
        pdlIdent.gruppe = PdlGruppe.FOLKEREGISTERIDENT
        val pdlIdenter = PdlIdenter()
        pdlIdenter.identer = listOf(pdlIdent)
        return pdlIdenter
    }
}
