package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ManuellRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(ManuellRegistreringService.class);

    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final Norg2Gateway norg2Gateway;

    public ManuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            Norg2Gateway norg2Gateway) {
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.norg2Gateway = norg2Gateway;
    }

    public void lagreManuellRegistrering(
            String veilederIdent,
            String veilederEnhetId,
            long registreringId,
            BrukerRegistreringType brukerRegistreringType){

        final ManuellRegistrering manuellRegistrering = new ManuellRegistrering()
                .setRegistreringId(registreringId)
                .setBrukerRegistreringType(brukerRegistreringType)
                .setVeilederIdent(veilederIdent)
                .setVeilederEnhetId(veilederEnhetId);

        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering);
    }

    public Veileder hentManuellRegistreringVeileder(long registreringId, BrukerRegistreringType brukerRegistreringType) {
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
            Map<Enhetnr, NavEnhet> enhetnrNavEnhetMap = norg2Gateway.hentAlleEnheter();

            NavEnhet navEnhet = enhetnrNavEnhetMap.get(enhetId);

            return Optional.ofNullable(navEnhet);
        } catch (Exception e) {
            LOG.error("Feil ved henting av NAV-enheter fra den nye Organisasjonsenhet-tjenesten.", e);
            return Optional.empty();
        }
    }
}
