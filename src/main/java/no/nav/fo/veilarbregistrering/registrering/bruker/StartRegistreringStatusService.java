package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.metrics.Events;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDtoMapper.map;

public class StartRegistreringStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(StartRegistreringStatusService.class);

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final BrukerTilstandService brukerTilstandService;
    private final PersonGateway personGateway;
    private MetricsService metricsService;

    public StartRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PersonGateway personGateway,
            MetricsService metricsService) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.brukerTilstandService = brukerTilstandService;
        this.personGateway = personGateway;
        this.metricsService = metricsService;
    }

    public StartRegistreringStatusDto hentStartRegistreringStatus(Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());

        Optional<GeografiskTilknytning> muligGeografiskTilknytning = hentGeografiskTilknytning(bruker);

        muligGeografiskTilknytning.ifPresent(geografiskTilknytning -> {
            metricsService.reportFields(Events.START_REGISTRERING_EVENT, brukersTilstand, geografiskTilknytning);
        });

        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        Boolean oppfyllerBetingelseOmArbeidserfaring = null;
        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            oppfyllerBetingelseOmArbeidserfaring =
                    arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer())
                            .harJobbetSammenhengendeSeksAvTolvSisteManeder(now());
        }

        StartRegistreringStatusDto startRegistreringStatus = map(
                brukersTilstand,
                muligGeografiskTilknytning,
                oppfyllerBetingelseOmArbeidserfaring,
                bruker.getGjeldendeFoedselsnummer().alder(now()));

        LOG.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private Optional<GeografiskTilknytning> hentGeografiskTilknytning(Bruker bruker) {
        Optional<GeografiskTilknytning> geografiskTilknytning = Optional.empty();
        try {
            long t1 = System.currentTimeMillis();
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker.getGjeldendeFoedselsnummer());
            LOG.info("Henting av geografisk tilknytning tok {} ms.", System.currentTimeMillis() - t1);

        } catch (RuntimeException e) {
            LOG.warn("Hent geografisk tilknytning fra TPS feilet. Skal ikke påvirke annen bruk.", e);
        }

        return geografiskTilknytning;
    }
}