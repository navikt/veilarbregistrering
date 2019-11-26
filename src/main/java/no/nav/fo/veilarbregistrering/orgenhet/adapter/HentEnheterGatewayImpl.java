package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOppgavebehandlerfilter;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentFullstendigEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentFullstendigEnhetListeResponse;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ALLE_ENHETER;

public class HentEnheterGatewayImpl implements HentEnheterGateway {

    private OrganisasjonEnhetV2 organisasjonEnhetV2;

    public HentEnheterGatewayImpl(OrganisasjonEnhetV2 organisasjonEnhetV2){
        this.organisasjonEnhetV2 = organisasjonEnhetV2;
    }

    @Override
    @Cacheable(HENT_ALLE_ENHETER)
    public List<NavEnhet> hentAlleEnheter() {
        WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse =
                organisasjonEnhetV2.hentFullstendigEnhetListe(lagHentFullstendigEnhetListeRequest());

        return hentFullstendigEnhetListeResponse.getEnhetListe().stream()
                .map(HentEnheterGatewayImpl::orgEnhetTilPortefoljeEnhet)
                .collect(Collectors.toList());
    }

    private WSHentFullstendigEnhetListeRequest lagHentFullstendigEnhetListeRequest() {
        final WSHentFullstendigEnhetListeRequest request = new WSHentFullstendigEnhetListeRequest();
        request.setOppgavebehandlerfilter(WSOppgavebehandlerfilter.UFILTRERT);

        return request;
    }

    private static NavEnhet orgEnhetTilPortefoljeEnhet(WSOrganisasjonsenhet orgEnhet) {
        return new NavEnhet(orgEnhet.getEnhetId(), orgEnhet.getEnhetNavn());
    }

}
