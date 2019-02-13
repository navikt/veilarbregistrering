package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;

import no.nav.fo.veilarbregistrering.domain.NavEnhet;

@Slf4j
public class EnhetOppslagService {

    private HentEnheterService hentEnheterService;

    public EnhetOppslagService(HentEnheterService hentEnheterService){
        this.hentEnheterService = hentEnheterService;
    }

    public NavEnhet finnEnhet(String enhetId) {
        try {
            return hentEnheterService.hentAlleEnheter()
                    .stream()
                    .filter((enhet) -> enhet.getId().equals(enhetId))
                    .findFirst().orElseThrow(RuntimeException::new);

        } catch (Exception e) {
            return null;
        }
    }

}
