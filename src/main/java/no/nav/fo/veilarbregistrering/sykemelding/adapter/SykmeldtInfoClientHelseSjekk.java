package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;

import static no.nav.fo.veilarbregistrering.helsesjekk.HealthCheck.performHealthCheck;
import static no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig.INFOTRYGDAPI_URL_PROPERTY_NAME;

public class SykmeldtInfoClientHelseSjekk implements HealthCheck {

    private String infotrygdFOPingUrl = INFOTRYGDAPI_URL_PROPERTY_NAME + "/rest/internal/isAlive";

    @Override
    public HealthCheckResult checkHealth() {
        return performHealthCheck(infotrygdFOPingUrl);
    }
}