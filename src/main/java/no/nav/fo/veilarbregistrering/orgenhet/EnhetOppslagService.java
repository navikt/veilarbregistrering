package no.nav.fo.veilarbregistrering.orgenhet;

import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGateway;

@Slf4j
public class EnhetOppslagService {

    private HentEnheterGateway hentEnheterGateway;

    public EnhetOppslagService(HentEnheterGateway hentEnheterGateway){
        this.hentEnheterGateway = hentEnheterGateway;
    }

    public NavEnhet finnEnhet(String enhetId) {
        try {
            return hentEnheterGateway.hentAlleEnheter()
                    .stream()
                    .filter((enhet) -> enhet.getId().equals(enhetId))
                    .findFirst().orElseThrow(RuntimeException::new);

        } catch (Exception e) {
            return null;
        }
    }

}
