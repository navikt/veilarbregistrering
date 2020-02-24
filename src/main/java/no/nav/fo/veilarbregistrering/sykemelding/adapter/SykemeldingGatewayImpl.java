package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.sykemelding.Maksdato;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;

public class SykemeldingGatewayImpl implements SykemeldingGateway {

    public final SykmeldtInfoClient sykmeldtInfoClient;

    public SykemeldingGatewayImpl(SykmeldtInfoClient sykmeldtInfoClient) {
        this.sykmeldtInfoClient = sykmeldtInfoClient;
    }

    @Override
    public Maksdato hentReberegnetMaksdato(Foedselsnummer fnr) {
        InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);

        if (infotrygdData == null || infotrygdData.maksDato == null) {
            return Maksdato.nullable();
        }

        return Maksdato.of(infotrygdData.maksDato);
    }
}
