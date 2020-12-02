package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;

import java.util.Collections;

public class HentRegistreringService {

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private ProfileringRepository profileringRepository;
    private ManuellRegistreringService manuellRegistreringService;

    public HentRegistreringService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            ManuellRegistreringService manuellRegistreringService) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.manuellRegistreringService = manuellRegistreringService;
    }

    public OrdinaerBrukerRegistrering hentOrdinaerBrukerRegistrering(Bruker bruker) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.getAktorId(), Status.OVERFORT_ARENA, Status.PUBLISERT_KAFKA, Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND);

        if (ordinaerBrukerRegistrering == null) {
            return null;
        }

        Profilering profilering = profileringRepository.hentProfileringForId(
                ordinaerBrukerRegistrering.getId());
        ordinaerBrukerRegistrering.setProfilering(profilering);

        Veileder veileder = manuellRegistreringService.hentManuellRegistreringVeileder(
                ordinaerBrukerRegistrering.getId(), ordinaerBrukerRegistrering.hentType());
        ordinaerBrukerRegistrering.setManueltRegistrertAv(veileder);

        return ordinaerBrukerRegistrering;
    }

    public SykmeldtRegistrering hentSykmeldtRegistrering(Bruker bruker) {
        SykmeldtRegistrering sykmeldtBrukerRegistrering = brukerRegistreringRepository
                .hentSykmeldtregistreringForAktorId(bruker.getAktorId());

        if (sykmeldtBrukerRegistrering == null) {
            return null;
        }
        Veileder veileder = manuellRegistreringService.hentManuellRegistreringVeileder(
                sykmeldtBrukerRegistrering.getId(), sykmeldtBrukerRegistrering.hentType());
        sykmeldtBrukerRegistrering.setManueltRegistrertAv(veileder);

        return sykmeldtBrukerRegistrering;
    }
}
