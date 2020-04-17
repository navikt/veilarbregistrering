package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metrics;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPHOLDSTILLATELSE_EVENT;

public class OppholdstillatelseServiceImpl implements OppholdstillatelseService {

    private static final Logger LOG = LoggerFactory.getLogger(OppholdstillatelseServiceImpl.class);

    private PdlOppslagGateway pdlOppslagGateway;
    private UnleashService unleashService;

    public OppholdstillatelseServiceImpl(PdlOppslagGateway pdlOppslagGateway, UnleashService unleashService) {
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.unleashService = unleashService;
    }

    @Override
    public void hentOgSammenlignOppholdFor(AktorId aktorid) {
        if (!pdlIsEnabled()) {
            return;
        }

        try {
            Person person = pdlOppslagGateway.hentPerson(aktorid);
            LOG.info("Persondata fra PDL: {}", person);
            Metrics.reportSimple(OPPHOLDSTILLATELSE_EVENT, person.getStatsborgerskap(), person.getOpphold());

        } catch (Exception e) {
            LOG.error("Feil ved henting av data fra PDL", e);
        }
    }

    private boolean pdlIsEnabled() {
        return unleashService.isEnabled("veilarbregistrering.pdlEnabled");
    }

}
