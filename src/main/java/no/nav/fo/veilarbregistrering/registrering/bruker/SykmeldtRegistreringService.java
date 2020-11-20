package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Optional.ofNullable;

public class SykmeldtRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(SykmeldtRegistreringService.class);

    private final BrukerTilstandService brukerTilstandService;
    private final OppfolgingGateway oppfolgingGateway;
    private final BrukerRegistreringRepository brukerRegistreringRepository;

    public SykmeldtRegistreringService(
            BrukerTilstandService brukerTilstandService,
            OppfolgingGateway oppfolgingGateway,
            BrukerRegistreringRepository brukerRegistreringRepository) {
        this.brukerTilstandService = brukerTilstandService;
        this.oppfolgingGateway = oppfolgingGateway;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());

        if (brukersTilstand.ikkeErSykemeldtRegistrering()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getGjeldendeFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, bruker.getAktorId());
        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

        return id;
    }
}
