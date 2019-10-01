package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Oppgavebehandlerfilter;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Organisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.HentFullstendigEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.HentFullstendigEnhetListeResponse;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ALLE_ENHETER;

public class HentEnheterGatewayImpl implements HentEnheterGateway {

    private final OrganisasjonEnhetV2 organisasjonEnhetV2;

    public HentEnheterGatewayImpl(OrganisasjonEnhetV2 organisasjonEnhetV2){
        this.organisasjonEnhetV2 = organisasjonEnhetV2;
    }

    @Override
    @Cacheable(HENT_ALLE_ENHETER)
    public List<NavEnhet> hentAlleEnheter() {
        HentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse =
                organisasjonEnhetV2.hentFullstendigEnhetListe(lagHentFullstendigEnhetListeRequest());

        return hentFullstendigEnhetListeResponse.getEnhetListe().stream()
                .map(HentEnheterGatewayImpl::orgEnhetTilPortefoljeEnhet)
                .collect(Collectors.toList());
    }

    private HentFullstendigEnhetListeRequest lagHentFullstendigEnhetListeRequest() {
        final HentFullstendigEnhetListeRequest request = new HentFullstendigEnhetListeRequest();
        request.setOppgavebehandlerfilter(Oppgavebehandlerfilter.UFILTRERT);

        return request;
    }

    private static NavEnhet orgEnhetTilPortefoljeEnhet(Organisasjonsenhet orgEnhet) {
        return new NavEnhet(orgEnhet.getEnhetId(), orgEnhet.getEnhetNavn());
    }

}
