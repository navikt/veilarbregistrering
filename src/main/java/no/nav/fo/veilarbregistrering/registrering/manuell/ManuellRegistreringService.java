package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ManuellRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(ManuellRegistreringService.class);

    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final HentEnheterGateway hentEnheterGateway;
    private final Norg2Gateway norg2Gateway;
    private final UnleashService unleashService;

    public ManuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            HentEnheterGateway hentEnheterGateway,
            Norg2Gateway norg2Gateway,
            UnleashService unleashService) {
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.hentEnheterGateway = hentEnheterGateway;
        this.norg2Gateway = norg2Gateway;
        this.unleashService = unleashService;
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

    public Veileder hentManuellRegistreringVeileder(long registreringId, BrukerRegistreringType brukerRegistreringType){

        ManuellRegistrering registrering = manuellRegistreringRepository
                .hentManuellRegistrering(registreringId, brukerRegistreringType);

        if (registrering == null) {
            return null;
        }

        Optional<NavEnhet> enhet = finnEnhet(Enhetnr.of(registrering.getVeilederEnhetId()));

        if (norg2ViaRest()) {
            try {
                Optional<NavEnhet> navEnhet = finnEnhetViaRest(Enhetnr.of(registrering.getVeilederEnhetId()));
                LOG.info(String.format("Lik nav-enhet: %s, SOAP: %s, Rest: %s", enhet.equals(navEnhet) ? "Ja" : "Nei", enhet, navEnhet));
            } catch (RuntimeException e) {
                LOG.error("Sammenligning av NavEnhet feilet. Har ingen betydning", e);
            }
        }

        return new Veileder()
                .setEnhet(enhet.orElse(null))
                .setIdent(registrering.getVeilederIdent());
    }

    private boolean norg2ViaRest() {
        return unleashService.isEnabled("veilarbregistrering.registrering.norg2viaRest");
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

    Optional<NavEnhet> finnEnhetViaRest(Enhetnr enhetId) {
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
