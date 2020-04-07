package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatakvalitetOppholdstillatelseServiceImpl implements DatakvalitetOppholdstillatelseService {

    private static final Logger LOG = LoggerFactory.getLogger(DatakvalitetOppholdstillatelseServiceImpl.class);

    private PdlOppslagGateway pdlOppslagGateway;

    public DatakvalitetOppholdstillatelseServiceImpl(PdlOppslagGateway pdlOppslagGateway) {
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    @Override
    public void hentOgSammenlignOppholdFor(AktorId aktorid) {
        LOG.info("hentOgSammenlignOppholdFor {}", aktorid.hashCode());
        try {
            Person person = pdlOppslagGateway.hentPerson(aktorid);
            LOG.info("Persondata fra PDL: {}", person);

        } catch (Exception e) {
            LOG.error("Feil ved henting av data fra PDL", e);
        }
    }
}
