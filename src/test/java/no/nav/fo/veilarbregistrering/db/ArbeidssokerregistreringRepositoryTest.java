package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.HelseHinderSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.Date;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ArbeidssokerregistreringRepositoryTest extends IntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        arbeidssokerregistreringRepository = new ArbeidssokerregistreringRepository(jdbcTemplate);
    }

    @Test
    public void registrerBruker() {

        Date opprettetDato = new Date(System.currentTimeMillis());
        AktorId aktorId = new AktorId("11111");
        BrukerRegistrering bruker = new BrukerRegistrering()
                .setNusKode("nus12")
                .setSisteStilling(new Stilling().setStyrk08("12345"))
                .setOpprettetDato(opprettetDato)
                .setEnigIOppsummering(true)
                .setOppsummering("Test test oppsummering")
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI));

        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);

        assertRegistrertBruker(bruker, brukerRegistrering);
    }

    private void assertRegistrertBruker(BrukerRegistrering bruker, BrukerRegistrering brukerRegistrering) {
        assertThat(brukerRegistrering.getNusKode()).isEqualTo(bruker.getNusKode());
        assertThat(brukerRegistrering.isEnigIOppsummering()).isEqualTo(bruker.isEnigIOppsummering());
        assertThat(brukerRegistrering.getOppsummering()).isEqualTo(bruker.getOppsummering());
        assertThat(brukerRegistrering.getSisteStilling()).isEqualTo(bruker.getSisteStilling());
        assertThat(brukerRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());

    }
}