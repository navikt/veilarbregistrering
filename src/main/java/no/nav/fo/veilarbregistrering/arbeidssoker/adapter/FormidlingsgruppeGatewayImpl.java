package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
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
    public List<Arbeidssokerperiode> finnArbeissokerperioder(Foedselsnummer foedselsnummer, Periode periode) {
        return formidlingsgruppeRestClient.hentFormidlingshistorikk(foedselsnummer, periode)
                .map(FormidlingshistorikkMapper::map)
                .orElse(Collections.emptyList());
    }
}
