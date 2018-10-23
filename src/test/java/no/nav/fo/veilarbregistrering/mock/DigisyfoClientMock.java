package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.SykeforloepMetaData;
import no.nav.fo.veilarbregistrering.httpclient.DigisyfoClient;

public class DigisyfoClientMock extends DigisyfoClient {

    public DigisyfoClientMock() {
        super(null);
    }

    @Override
    public SykeforloepMetaData hentSykeforloepMetadata() {
        return new SykeforloepMetaData().withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
    }
}
