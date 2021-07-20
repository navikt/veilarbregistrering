package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.metrics.Events;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatusMetrikker.rapporterRegistreringsstatus;
import static no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDtoMapper.map;

public class StartRegistreringStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(StartRegistreringStatusService.class);

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final BrukerTilstandService brukerTilstandService;
    private final PdlOppslagGateway pdlOppslagGateway;
    private final InfluxMetricsService influxMetricsService;

    public StartRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PdlOppslagGateway pdlOppslagGateway,
            InfluxMetricsService influxMetricsService) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.brukerTilstandService = brukerTilstandService;
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.influxMetricsService = influxMetricsService;
    }

    public StartRegistreringStatusDto hentStartRegistreringStatus(Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker);

        Optional<GeografiskTilknytning> muligGeografiskTilknytning = hentGeografiskTilknytning(bruker);

        muligGeografiskTilknytning.ifPresent(geografiskTilknytning ->
                influxMetricsService.reportFields(Events.START_REGISTRERING_EVENT, brukersTilstand, geografiskTilknytning));

        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        Boolean oppfyllerBetingelseOmArbeidserfaring = null;
        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            oppfyllerBetingelseOmArbeidserfaring =
                    arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer())
                            .harJobbetSammenhengendeSeksAvTolvSisteManeder(now());
        }

        StartRegistreringStatusDto startRegistreringStatus = map(
                brukersTilstand,
                muligGeografiskTilknytning.orElse(null),
                oppfyllerBetingelseOmArbeidserfaring,
                bruker.getGjeldendeFoedselsnummer().alder(now()));

        LOG.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        rapporterRegistreringsstatus(influxMetricsService, startRegistreringStatus);

        return startRegistreringStatus;
    }

    private Optional<GeografiskTilknytning> hentGeografiskTilknytning(Bruker bruker) {
        Optional<GeografiskTilknytning> geografiskTilknytning = Optional.empty();
        try {
            long t1 = System.currentTimeMillis();
            geografiskTilknytning = pdlOppslagGateway.hentGeografiskTilknytning(bruker.getAktorId());
            LOG.info("Henting av geografisk tilknytning tok {} ms.", System.currentTimeMillis() - t1);

        } catch (RuntimeException e) {
            LOG.warn("Hent geografisk tilknytning fra TPS feilet. Skal ikke påvirke annen bruk.", e);
        }

        return geografiskTilknytning;
    }
}