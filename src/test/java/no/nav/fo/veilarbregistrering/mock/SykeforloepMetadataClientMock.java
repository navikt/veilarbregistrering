package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.SykeforloepMetaData;
import no.nav.fo.veilarbregistrering.httpclient.DigisyfoClient;

public class SykeforloepMetadataClientMock extends DigisyfoClient {

    public SykeforloepMetadataClientMock() {
        super(null);
    }

    @Override
    public SykeforloepMetaData hentSykeforloepMetadata() {
        return new SykeforloepMetaData().withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
    }
}
