package no.nav.fo.veilarbregistrering.registrering.manuell;

import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.orgenhet.EnhetOppslagService;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ManuellRegistreringService {

    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final EnhetOppslagService enhetOppslagService;

    public ManuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            EnhetOppslagService enhetOppslagService) {
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.enhetOppslagService = enhetOppslagService;
    }

    public void lagreManuellRegistrering(String veilederIdent, String veilederEnhetId,
                                         long registreringId, BrukerRegistreringType brukerRegistreringType){

        final ManuellRegistrering manuellRegistrering = new ManuellRegistrering()
                .setRegistreringId(registreringId)
                .setBrukerRegistreringType(brukerRegistreringType)
                .setVeilederIdent(veilederIdent)
                .setVeilederEnhetId(veilederEnhetId);

        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering);

    }

    public Veileder hentManuellRegistreringVeileder(long registreringId, BrukerRegistreringType brukerRegistreringType){

        ManuellRegistrering registrering = manuellRegistreringRepository
                .hentManuellRegistrering(registreringId, brukerRegistreringType);

        if (registrering == null) {
            return null;
        }

        NavEnhet enhet = enhetOppslagService.finnEnhet(registrering.getVeilederEnhetId());

        return new Veileder()
                .setEnhet(enhet)
                .setIdent(registrering.getVeilederIdent());

    }

}
