package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder;
import no.nav.fo.veilarbregistrering.registrering.formidling.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.bruker.Resending.kanResendes;

public class HentRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(HentRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final SykmeldtRegistreringRepository sykmeldtRegistreringRepository;
    private final ProfileringRepository profileringRepository;
    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final Norg2Gateway norg2Gateway;

    public HentRegistreringService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            SykmeldtRegistreringRepository sykmeldtRegistreringRepository,
            ProfileringRepository profileringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            Norg2Gateway norg2Gateway) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.sykmeldtRegistreringRepository = sykmeldtRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.norg2Gateway = norg2Gateway;
    }

    public OrdinaerBrukerRegistrering hentOrdinaerBrukerRegistrering(Bruker bruker) {
        return hentOrdinaerBrukerRegistrering(bruker,
                List.of(Status.OVERFORT_ARENA, Status.PUBLISERT_KAFKA, Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND));
    }

    public OrdinaerBrukerRegistrering hentIgangsattOrdinaerBrukerRegistrering(Bruker bruker) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = hentOrdinaerBrukerRegistrering(bruker,
                List.of(Status.DOD_UTVANDRET_ELLER_FORSVUNNET, Status.MANGLER_ARBEIDSTILLATELSE));
        return kanResendes(ordinaerBrukerRegistrering) ? ordinaerBrukerRegistrering : null;
    }

    private OrdinaerBrukerRegistrering hentOrdinaerBrukerRegistrering(Bruker bruker, List<Status> status) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.getAktorId(), status)
                .stream()
                .findFirst()
                .orElse(null);

        if (ordinaerBrukerRegistrering == null) {
            return null;
        }

        Veileder veileder = hentManuellRegistreringVeileder(
                ordinaerBrukerRegistrering.getId(), ordinaerBrukerRegistrering.hentType());
        ordinaerBrukerRegistrering.setManueltRegistrertAv(veileder);

        Profilering profilering = profileringRepository.hentProfileringForId(
                ordinaerBrukerRegistrering.getId());
        ordinaerBrukerRegistrering.setProfilering(profilering);

        return ordinaerBrukerRegistrering;
    }

    public SykmeldtRegistrering hentSykmeldtRegistrering(Bruker bruker) {
        SykmeldtRegistrering sykmeldtBrukerRegistrering = sykmeldtRegistreringRepository
                .hentSykmeldtregistreringForAktorId(bruker.getAktorId());

        if (sykmeldtBrukerRegistrering == null) {
            return null;
        }
        Veileder veileder = hentManuellRegistreringVeileder(
                sykmeldtBrukerRegistrering.getId(), sykmeldtBrukerRegistrering.hentType());
        sykmeldtBrukerRegistrering.setManueltRegistrertAv(veileder);

        return sykmeldtBrukerRegistrering;
    }

    public Veileder hentManuellRegistreringVeileder(long registreringId, BrukerRegistreringType brukerRegistreringType) {
        ManuellRegistrering registrering = manuellRegistreringRepository
                .hentManuellRegistrering(registreringId, brukerRegistreringType);

        if (registrering == null) {
            return null;
        }

        Optional<NavEnhet> enhet = finnEnhet(Enhetnr.Companion.of(registrering.getVeilederEnhetId()));

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