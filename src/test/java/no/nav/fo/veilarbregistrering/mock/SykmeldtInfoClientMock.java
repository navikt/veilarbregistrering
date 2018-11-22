package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.SykeforloepMetaData;
import no.nav.fo.veilarbregistrering.httpclient.SykmeldtInfoClient;

public class SykmeldtInfoClientMock extends SykmeldtInfoClient {

    public SykmeldtInfoClientMock() {
        super(null);
    }

    @Override
    public SykeforloepMetaData hentSykeforloepMetadata() {
        return new SykeforloepMetaData().withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
    }
}
