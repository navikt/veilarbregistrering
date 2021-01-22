package no.nav.fo.veilarbregistrering.bruker.pdl;


import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;

public class PdlOppslagClientHelsesjekk implements HealthCheck {

    private String baseUrl;

    public PdlOppslagClientHelsesjekk(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public HealthCheckResult checkHealth() {
        return no.nav.fo.veilarbregistrering.helsesjekk.resources.HealthCheck.performHealthCheck(baseUrl);
    }

}
