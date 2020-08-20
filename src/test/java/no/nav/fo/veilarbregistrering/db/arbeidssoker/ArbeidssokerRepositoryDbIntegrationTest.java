package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.*;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Arrays.asList;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("01234567890");
    private static final Foedselsnummer FOEDSELSNUMMER_2 = Foedselsnummer.of("01234567892");
    private static final Foedselsnummer FOEDSELSNUMMER_3 = Foedselsnummer.of("01234567895");

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
        EndretFormidlingsgruppeCommand command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20));

        long id = arbeidssokerRepository.lagre(command);
        assertThat(id).isNotNull();

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(FOEDSELSNUMMER);
        assertThat(arbeidssokerperiodes.asList()).hasSize(1);
    }

    @Test
    public void skal_hente_alle_periodene_for_en_persons_identer() {
        EndretFormidlingsgruppeCommand command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusDays(10));
        arbeidssokerRepository.lagre(command);

        EndretFormidlingsgruppeCommand command2 = endretFormdlingsgruppe(FOEDSELSNUMMER_2, LocalDateTime.now().minusDays(50));
        arbeidssokerRepository.lagre(command2);

        EndretFormidlingsgruppeCommand command3 = endretFormdlingsgruppe(FOEDSELSNUMMER_3, LocalDateTime.now().minusSeconds(20));
        arbeidssokerRepository.lagre(command3);

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(
                asList(FOEDSELSNUMMER, FOEDSELSNUMMER_2, FOEDSELSNUMMER_3));
        assertThat(arbeidssokerperiodes.asList()).hasSize(3);
    }


    private EndretFormidlingsgruppeCommand endretFormdlingsgruppe(Foedselsnummer foedselsnummer, LocalDateTime tidspunkt) {
        return new EndretFormidlingsgruppeCommand() {
            @Override
            public Optional<Foedselsnummer> getFoedselsnummer() {
                return Optional.of(foedselsnummer);
            }

            @Override
            public String getPersonId() {
                return "123456";
            }

            @Override
            public Operation getOperation() {
                return Operation.UPDATE;
            }

            @Override
            public Formidlingsgruppe getFormidlingsgruppe() {
                return Formidlingsgruppe.of("ARBS");
            }

            @Override
            public LocalDateTime getFormidlingsgruppeEndret() {
                return tidspunkt;
            }

            @Override
            public Optional<Formidlingsgruppe> getForrigeFormidlingsgruppe() {
                return Optional.empty();
            }

            @Override
            public Optional<LocalDateTime> getForrigeFormidlingsgruppeEndret() {
                return Optional.empty();
            }
        };
    }

}
