package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

public class ManuellRegistreringService {

    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final HentEnheterGateway hentEnheterGateway;

    public ManuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            HentEnheterGateway hentEnheterGateway) {
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.hentEnheterGateway = hentEnheterGateway;
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

        NavEnhet enhet = finnEnhet(registrering.getVeilederEnhetId());

        return new Veileder()
                .setEnhet(enhet)
                .setIdent(registrering.getVeilederIdent());

    }

    NavEnhet finnEnhet(String enhetId) {
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
