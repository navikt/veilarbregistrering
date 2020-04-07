package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatakvalitetOppholdstillatelseServiceImpl implements DatakvalitetOppholdstillatelseService {

    private static final Logger LOG = LoggerFactory.getLogger(DatakvalitetOppholdstillatelseServiceImpl.class);

    private PdlOppslagGateway pdlOppslagGateway;
    private UnleashService unleashService;

    public DatakvalitetOppholdstillatelseServiceImpl(PdlOppslagGateway pdlOppslagGateway, UnleashService unleashService) {
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.unleashService = unleashService;
    }

    @Override
    public void hentOgSammenlignOppholdFor(AktorId aktorid) {
        LOG.info("hentOgSammenlignOppholdFor {}", aktorid.hashCode());

        if (!pdlIsEnabled()) {
            return;
        }
        try {
            Person person = pdlOppslagGateway.hentPerson(aktorid);
            LOG.info("Persondata fra PDL: {}", person);

        } catch (Exception e) {
            LOG.error("Feil ved henting av data fra PDL", e);
        }
    }

    private boolean pdlIsEnabled() {
        return unleashService.isEnabled("veilarbregistrering.pdlEnabled");
    }

}
