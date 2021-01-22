package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;
import static no.nav.fo.veilarbregistrering.helsesjekk.HealthCheck.performHealthCheck;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig.OPPFOLGING_API_PROPERTY_NAME;

public class OppfolgingClientHelseSjekk implements HealthCheck {

    private String veilarboppfolgingPingUrl = getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME) + "/ping";

    @Override
    public HealthCheckResult checkHealth() {
        return performHealthCheck(veilarboppfolgingPingUrl);
    }
}