package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter.FormidlingsgruppeGatewayImpl
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter.FormidlingsgruppeRestClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FormidlingsgruppeGatewayConfig {

    @Bean
    fun formidlingsgruppeRestClient(): FormidlingsgruppeRestClient = mockk()

    @Bean
    fun formidlingsgruppeGateway(formidlingsgruppeRestClient: FormidlingsgruppeRestClient): FormidlingsgruppeGateway {
        return FormidlingsgruppeGatewayImpl(formidlingsgruppeRestClient)
    }
}
