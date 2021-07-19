package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FormidlingsgruppeGatewayConfig {

    @Bean
    fun formidlingsgruppeRestClient(): FormidlingsgruppeRestClient {
        return FormidlingsgruppeRestClient(null, null)
    }

    @Bean
    fun formidlingsgruppeGateway(
        formidlingsgruppeRestClient: FormidlingsgruppeRestClient
    ): FormidlingsgruppeGateway {
        return FormidlingsgruppeGatewayImpl(formidlingsgruppeRestClient)
    }
}
