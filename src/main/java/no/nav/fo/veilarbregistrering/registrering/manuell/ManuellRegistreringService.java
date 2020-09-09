package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ManuellRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(ManuellRegistreringService.class);

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

        Optional<NavEnhet> enhet = finnEnhet(Enhetnr.of(registrering.getVeilederEnhetId()));

        return new Veileder()
                .setEnhet(enhet.orElse(null))
                .setIdent(registrering.getVeilederIdent());
    }

    Optional<NavEnhet> finnEnhet(Enhetnr enhetId) {
        try {
            return hentEnheterGateway.hentAlleEnheter()
                    .stream()
                    .filter((enhet) -> enhet.getId().equals(enhetId))
                    .findFirst();

        } catch (Exception e) {
            LOG.error("Feil ved henting av NAV-enheter fra Organisasjonsenhet-tjenesten.", e);
            return Optional.empty();
        }
    }

}
