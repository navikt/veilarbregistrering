package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.*;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig
@Transactional(transactionManager = "txMgrTest")
@ContextConfiguration(classes = {DatabaseConfig.class, RepositoryConfig.class})
public class ArbeidssokerRepositoryDbIntegrationTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("01234567890");
    private static final Foedselsnummer FOEDSELSNUMMER_2 = Foedselsnummer.of("01234567892");
    private static final Foedselsnummer FOEDSELSNUMMER_3 = Foedselsnummer.of("01234567895");

    @Autowired
    private ArbeidssokerRepository arbeidssokerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    public void setup() {
        MigrationUtils.createTables(jdbcTemplate);
    }

    @Test
    public void skal_kun_lagre_melding_en_gang() {
        EndretFormidlingsgruppeCommand command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20));

        long id = arbeidssokerRepository.lagre(command);
        assertThat(id).isNotNull();

        id = arbeidssokerRepository.lagre(command);

        assertThat(id).isEqualTo(-1);
    }

    @Test
    public void skal_lagre_formidlingsgruppeEvent() {
        EndretFormidlingsgruppeCommand command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusSeconds(20));

        long id = arbeidssokerRepository.lagre(command);
        assertThat(id).isNotNull();

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(FOEDSELSNUMMER);

        Arbeidssokerperiode arbeidssokerperiode = Arbeidssokerperiode.of(Formidlingsgruppe.of("ARBS"), Periode.of(LocalDate.now(), null));
        assertThat(arbeidssokerperiodes.asList()).containsOnly(arbeidssokerperiode);
    }

    @Test
    public void skal_hente_alle_periodene_for_en_persons_identer() {
        EndretFormidlingsgruppeCommand command = endretFormdlingsgruppe(FOEDSELSNUMMER, LocalDateTime.now().minusDays(10));
        arbeidssokerRepository.lagre(command);

        EndretFormidlingsgruppeCommand command2 = endretFormdlingsgruppe(FOEDSELSNUMMER_2, LocalDateTime.now().minusDays(50));
        arbeidssokerRepository.lagre(command2);

        EndretFormidlingsgruppeCommand command3 = endretFormdlingsgruppe(FOEDSELSNUMMER_3, LocalDateTime.now().minusSeconds(20));
        arbeidssokerRepository.lagre(command3);

        Bruker bruker = Bruker.of(FOEDSELSNUMMER, null, asList(FOEDSELSNUMMER_2, FOEDSELSNUMMER_3));

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerRepository.finnFormidlingsgrupper(bruker.alleFoedselsnummer());
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
            public String getPersonIdStatus() { return "AKTIV"; }

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
