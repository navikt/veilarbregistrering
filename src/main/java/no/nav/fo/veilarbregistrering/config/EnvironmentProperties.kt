package no.nav.fo.veilarbregistrering.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.env")
data class EnvironmentProperties(
    val openAmDiscoveryUrl: String,
    val veilarbloginOpenAmClientId: String,
    val openAmRefreshUrl: String,
    val openAmRedirectUrl: String,
    val openAmIssoRpUsername: String,
    val openAmIssoRpPassword: String,
    val aadDiscoveryUrl: String,
    val veilarbloginAadClientId: String,
    val loginserviceIdportenAudience: String,
    val loginserviceIdportenDiscoveryUrl: String,
    val naisStsDiscoveryUrl: String,
    val naisStsClientId: String,
    val abacUrl: String,
    val norg2Url: String,
    val aktorregisterUrl: String,
    val soapStsUrl: String,
    val arbeidsrettetDialogUrl: String,
    val kafkaBrokersUrl: String,

    // SOAP Endpoints
    val ytelseskontraktV3Endpoint: String,
    val varselOppgaveV1Endpoint: String,
    val behandleArbeidssoekerV1Endpoint: String)