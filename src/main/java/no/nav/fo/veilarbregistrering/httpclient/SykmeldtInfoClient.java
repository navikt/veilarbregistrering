package no.nav.fo.veilarbregistrering.httpclient;

import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.SykmeldtInfoData;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class SykmeldtInfoClient extends BaseClient {

    @Inject
    public SykmeldtInfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super("TEMP_INFOTRYGD_URL", httpServletRequestProvider);
    }

    public SykmeldtInfoData hentSykmeldtInfoData() {
        // mock, venter på infotryd api for å hente maxdato
        return new SykmeldtInfoData()
                .withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true)
                .withSykmeldtFraDato("2018-01-21");
    }

}