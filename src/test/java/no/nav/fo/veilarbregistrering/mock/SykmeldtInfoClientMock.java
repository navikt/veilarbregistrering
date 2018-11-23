package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.SykmeldtInfoData;
import no.nav.fo.veilarbregistrering.httpclient.SykmeldtInfoClient;

public class SykmeldtInfoClientMock extends SykmeldtInfoClient {

    public SykmeldtInfoClientMock() {
        super(null);
    }

    @Override
    public SykmeldtInfoData hentSykmeldtInfoData() {
        return new SykmeldtInfoData().withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
    }
}
