package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatakvalitetOppholdstillatelseServiceImpl implements DatakvalitetOppholdstillatelseService {

    private static final Logger LOG = LoggerFactory.getLogger(DatakvalitetOppholdstillatelseServiceImpl.class);

    @Override
    public void hentOgSammenlignOppholdFor(AktorId aktorid) {
        LOG.info("hentOgSammenlignOppholdFor {}", aktorid.hashCode());
    }
}
