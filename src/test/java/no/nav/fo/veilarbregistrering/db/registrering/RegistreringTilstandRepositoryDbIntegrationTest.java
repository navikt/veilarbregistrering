package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandTestdataBuilder.registreringTilstand;
import static no.nav.fo.veilarbregistrering.registrering.tilstand.Status.*;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistreringTilstandRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    private static final AktorId AKTOR_ID_11111 = AktorId.of("11111");
    private static final Bruker BRUKER_1 = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111);

    @Inject
    private JdbcTemplate jdbcTemplate;

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private RegistreringTilstandRepository registreringTilstandRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        brukerRegistreringRepository = new BrukerRegistreringRepositoryImpl(jdbcTemplate);
        registreringTilstandRepository = new RegistreringTilstandRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void skal_kaste_DataIntegrityViolationException_hvis_registreringstilstand_lagres_uten_at_registrering_er_lagret() {
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();

        assertThrows(DataIntegrityViolationException.class, () -> registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, registrering.getId())));
    }

    @Test
    public void skal_lagre_og_hente_registreringTilstand() {
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1);

        RegistreringTilstand registreringTilstand = RegistreringTilstand.medStatus(Status.MOTTATT, lagretRegistrering.getId());

        long id = registreringTilstandRepository.lagre(registreringTilstand);

        assertThat(id).isNotNegative();

        RegistreringTilstand lagretTilstand = registreringTilstandRepository.hentRegistreringTilstand(id);

        assertThat(lagretTilstand.getId()).isEqualTo(id);
        assertThat(lagretTilstand.getBrukerRegistreringId()).isEqualTo(lagretRegistrering.getId());
        assertThat(lagretTilstand.getOpprettet()).isBetween(now().minusSeconds(10), now().plusSeconds(10));
        assertThat(lagretTilstand.getSistEndret()).isNull();
        assertThat(lagretTilstand.getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void finnRegistreringTilstandMed_skal_returnere_alle_tilstander_med_angitt_status() {
        OrdinaerBrukerRegistrering lagretRegistrering1 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);

        RegistreringTilstand tilstand1 = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering1.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(MOTTATT)
                .build();
        registreringTilstandRepository.lagre(tilstand1);

        OrdinaerBrukerRegistrering lagretRegistrering2 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);

        RegistreringTilstand tilstand2 = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering2.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(PUBLISERT_KAFKA)
                .build();
        registreringTilstandRepository.lagre(tilstand2);

        List<RegistreringTilstand> mottatteRegistreringer = registreringTilstandRepository.finnRegistreringTilstanderMed(MOTTATT);
        assertThat(mottatteRegistreringer).hasSize(1);
    }

    @Test
    public void skal_returnere_neste_registrering_klar_for_publisering() {
        OrdinaerBrukerRegistrering nyesteRegistrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering eldsteRegistrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretNyesteRegistrering = brukerRegistreringRepository.lagre(nyesteRegistrering, BRUKER_1);
        OrdinaerBrukerRegistrering lagretEldsteRegistrering = brukerRegistreringRepository.lagre(eldsteRegistrering, BRUKER_1);

        RegistreringTilstand nyesteRegistreringTilstand = registreringTilstand()
                .brukerRegistreringId(lagretNyesteRegistrering.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(OVERFORT_ARENA)
                .build();
        registreringTilstandRepository.lagre(nyesteRegistreringTilstand);

        RegistreringTilstand eldsteRegistreringTilstand = registreringTilstand()
                .brukerRegistreringId(lagretEldsteRegistrering.getId())
                .opprettet(LocalDateTime.now().minusMinutes(10))
                .status(OVERFORT_ARENA)
                .build();
        long eldsteRegistreringTilstandId = registreringTilstandRepository.lagre(eldsteRegistreringTilstand);

        Optional<RegistreringTilstand> nesteRegistreringKlarForPublisering = registreringTilstandRepository.finnNesteRegistreringTilstandMed(OVERFORT_ARENA);

        assertThat(nesteRegistreringKlarForPublisering.get().getId()).isEqualTo(eldsteRegistreringTilstandId);
    }

    @Test
    public void skal_returnere_empty_naar_ingen_klare_for_publisering() {
        OrdinaerBrukerRegistrering nyesteRegistrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering eldsteRegistrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretNyesteRegistrering = brukerRegistreringRepository.lagre(nyesteRegistrering, BRUKER_1);
        OrdinaerBrukerRegistrering lagretEldsteRegistrering = brukerRegistreringRepository.lagre(eldsteRegistrering, BRUKER_1);

        RegistreringTilstand nyesteRegistreringTilstand = registreringTilstand()
                .brukerRegistreringId(lagretNyesteRegistrering.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(PUBLISERT_KAFKA)
                .build();
        registreringTilstandRepository.lagre(nyesteRegistreringTilstand);

        RegistreringTilstand eldsteRegistreringTilstand = registreringTilstand()
                .brukerRegistreringId(lagretEldsteRegistrering.getId())
                .opprettet(LocalDateTime.now().minusMinutes(10))
                .status(PUBLISERT_KAFKA)
                .build();
        registreringTilstandRepository.lagre(eldsteRegistreringTilstand);

        Optional<RegistreringTilstand> nesteRegistreringKlarForPublisering = registreringTilstandRepository.finnNesteRegistreringTilstandMed(OVERFORT_ARENA);

        assertThat(nesteRegistreringKlarForPublisering).isEmpty();
    }

    @Test
    public void skal_telle_antall_registreringer_med_status() {
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1);

        final int antallPublisertKafka = 5;
        final int antallOverfortArena = 3;

        lagRegistreringTilstand(lagretRegistrering, PUBLISERT_KAFKA, antallPublisertKafka);
        lagRegistreringTilstand(lagretRegistrering, OVERFORT_ARENA, antallOverfortArena);

        assertThat(registreringTilstandRepository.hentAntall(PUBLISERT_KAFKA)).isEqualTo(antallPublisertKafka);
        assertThat(registreringTilstandRepository.hentAntall(OVERFORT_ARENA)).isEqualTo(antallOverfortArena);
        assertThat(registreringTilstandRepository.hentAntall(MOTTATT)).isEqualTo(0);
    }

    private void lagRegistreringTilstand(OrdinaerBrukerRegistrering registrering, Status status, int antall) {
        for (int i = 0; i < antall; i++) {
            RegistreringTilstand nyesteRegistreringTilstand = registreringTilstand()
                    .brukerRegistreringId(registrering.getId())
                    .opprettet(LocalDateTime.now().minusMinutes(5))
                    .status(status)
                    .build();
            registreringTilstandRepository.lagre(nyesteRegistreringTilstand);
        }
    }
}
