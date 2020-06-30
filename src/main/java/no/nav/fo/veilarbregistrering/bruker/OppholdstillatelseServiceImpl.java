package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPHOLDSTILLATELSE_EVENT;

public class OppholdstillatelseServiceImpl implements OppholdstillatelseService {

    private static final Logger LOG = LoggerFactory.getLogger(OppholdstillatelseServiceImpl.class);

    private final PdlOppslagGateway pdlOppslagGateway;

    public OppholdstillatelseServiceImpl(PdlOppslagGateway pdlOppslagGateway) {
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    @Override
    public void hentOgSammenlignOppholdFor(AktorId aktorid) {
        try {
            Optional<Person> person = pdlOppslagGateway.hentPerson(aktorid);
            person.ifPresent(p -> {
                LOG.info("Persondata fra PDL: {}", p);
                Metrics.reportSimple(OPPHOLDSTILLATELSE_EVENT, p.getStatsborgerskap(), p.getOpphold());
            });

        } catch (Exception e) {
            LOG.error("Feil ved henting av data fra PDL", e);
        }
    }
}
