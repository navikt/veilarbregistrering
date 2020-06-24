package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.Collections;
import java.util.List;

public class FormidlingsgruppeGatewayImpl implements FormidlingsgruppeGateway {

    private final FormidlingsgruppeRestClient formidlingsgruppeRestClient;

    public FormidlingsgruppeGatewayImpl(FormidlingsgruppeRestClient formidlingsgruppeRestClient) {
        this.formidlingsgruppeRestClient = formidlingsgruppeRestClient;
    }

    @Override
    public Arbeidssokerperioder finnArbeissokerperioder(Foedselsnummer foedselsnummer, Periode periode) {
        List<Arbeidssokerperiode> arbeidssokerperioder = formidlingsgruppeRestClient.hentFormidlingshistorikk(foedselsnummer, periode)
                .map(FormidlingshistorikkMapper::map)
                .orElse(Collections.emptyList());

        return new Arbeidssokerperioder(arbeidssokerperioder);
    }
}
