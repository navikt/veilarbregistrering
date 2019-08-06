package no.nav.fo.veilarbregistrering.orgenhet.adapter;

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

public class HentEnheterGateway {

    private OrganisasjonEnhetV2 organisasjonEnhetService;

    public HentEnheterGateway(OrganisasjonEnhetV2 organisasjonEnhetService){
        this.organisasjonEnhetService = organisasjonEnhetService;
    }

    @Cacheable(HENT_ALLE_ENHETER)
    public List<NavEnhet> hentAlleEnheter() {
        HentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse =
                organisasjonEnhetService.hentFullstendigEnhetListe(lagHentFullstendigEnhetListeRequest());

        return hentFullstendigEnhetListeResponse.getEnhetListe().stream()
                .map(HentEnheterGateway::orgEnhetTilPortefoljeEnhet)
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
