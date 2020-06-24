package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("01234567890");

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
        EndretFormidlingsgruppeCommand command = new EndretFormidlingsgruppeCommand() {
            @Override
            public Optional<Foedselsnummer> getFoedselsnummer() {
                return Optional.of(FOEDSELSNUMMER);
            }

            @Override
            public String getPerson_id() {
                return null;
            }

            @Override
            public Formidlingsgruppe getFormidlingsgruppe() {
                return Formidlingsgruppe.of("ARBS");
            }

            @Override
            public LocalDateTime getFormidlingsgruppeEndret() {
                return LocalDateTime.now().minusSeconds(20);
            }
        };

        long id = arbeidssokerRepository.lagre(command);
        assertThat(id).isNotNull();

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(FOEDSELSNUMMER);
        assertThat(arbeidssokerperiodes.asList()).hasSize(1);
    }

}
