package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArenaFormidlingsgruppeEvent;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ArbeidssokerRepository arbeidssokerRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        this.arbeidssokerRepository = new ArbeidssokerRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void skal_lagre_formidlingsgruppeEvent() {
        ArenaFormidlingsgruppeEvent arenaFormidlingsgruppeEvent = ArenaFormidlingsgruppeEvent.of(
                Foedselsnummer.of("01234567890"),
                Formidlingsgruppe.of("ARBS"),
                Timestamp.valueOf(LocalDateTime.now().minusSeconds(20))
        );

        long id = arbeidssokerRepository.lagre(arenaFormidlingsgruppeEvent);

        assertThat(id).isNotNull();
    }

}
