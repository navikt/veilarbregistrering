package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
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
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringTilstandTestdataBuilder.registreringTilstand;
import static no.nav.fo.veilarbregistrering.registrering.bruker.Status.ARENA_OK;
import static no.nav.fo.veilarbregistrering.registrering.bruker.Status.MOTTATT;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AktiveringTilstandRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    private static final AktorId AKTOR_ID_11111 = AktorId.of("11111");
    private static final Bruker BRUKER_1 = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111);

    @Inject
    private JdbcTemplate jdbcTemplate;

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private AktiveringTilstandRepository aktiveringTilstandRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        brukerRegistreringRepository = new BrukerRegistreringRepositoryImpl(jdbcTemplate);
        aktiveringTilstandRepository = new AktiveringTilstandRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void skal_kaste_DataIntegrityViolationException_hvis_registreringstilstand_lagres_uten_at_registrering_er_lagret() {
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();

        assertThrows(DataIntegrityViolationException.class, () -> aktiveringTilstandRepository.lagre(AktiveringTilstand.ofMottattRegistrering(registrering.getId())));
    }

    @Test
    public void skal_lagre_og_hente_registreringTilstand() {
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1);

        AktiveringTilstand registreringTilstand = AktiveringTilstand.ofMottattRegistrering(lagretRegistrering.getId());

        long id = aktiveringTilstandRepository.lagre(registreringTilstand);

        assertThat(id).isNotNegative();

        AktiveringTilstand lagretTilstand = aktiveringTilstandRepository.hentAktiveringTilstand(id);

        assertThat(lagretTilstand.getId()).isEqualTo(id);
        assertThat(lagretTilstand.getUuid()).isNotNull();
        assertThat(lagretTilstand.getBrukerRegistreringId()).isEqualTo(lagretRegistrering.getId());
        assertThat(lagretTilstand.getOpprettet()).isBetween(now().minusSeconds(10), now().plusSeconds(10));
        assertThat(lagretTilstand.getSistEndret()).isNull();
        assertThat(lagretTilstand.getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void skal_finne_den_eldste_tilstanden_med_status_mottatt() {
        OrdinaerBrukerRegistrering registrering1 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering registrering2 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering registrering3 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretRegistrering1 = brukerRegistreringRepository.lagre(registrering1, BRUKER_1);
        OrdinaerBrukerRegistrering lagretRegistrering2 = brukerRegistreringRepository.lagre(registrering2, BRUKER_1);
        OrdinaerBrukerRegistrering lagretRegistrering3 = brukerRegistreringRepository.lagre(registrering3, BRUKER_1);


        AktiveringTilstand eldsteRegistrering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering1.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .build();
        long id1 = aktiveringTilstandRepository.lagre(eldsteRegistrering);

        AktiveringTilstand nyesteRegistering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering2.getId())
                .opprettet(LocalDateTime.now().minusMinutes(1))
                .build();
        aktiveringTilstandRepository.lagre(nyesteRegistering);

        AktiveringTilstand midtersteRegistering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering3.getId())
                .opprettet(LocalDateTime.now().minusMinutes(3))
                .build();
        aktiveringTilstandRepository.lagre(midtersteRegistering);

        Optional<AktiveringTilstand> lagretTilstand = aktiveringTilstandRepository.finnNesteAktiveringTilstandForOverforing();

        assertThat(lagretTilstand.get().getId()).isEqualTo(id1);
        assertThat(lagretTilstand.get().getBrukerRegistreringId()).isEqualTo(lagretRegistrering1.getId());
        assertThat(lagretTilstand.get().getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void skal_returnere_empty_naar_ingen_flere_mottatte() {
        OrdinaerBrukerRegistrering registrering1 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering registrering2 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering registrering3 = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering lagretRegistrering1 = brukerRegistreringRepository.lagre(registrering1, BRUKER_1);
        OrdinaerBrukerRegistrering lagretRegistrering2 = brukerRegistreringRepository.lagre(registrering2, BRUKER_1);
        OrdinaerBrukerRegistrering lagretRegistrering3 = brukerRegistreringRepository.lagre(registrering3, BRUKER_1);

        AktiveringTilstand eldsteRegistrering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering1.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(ARENA_OK)
                .build();
        aktiveringTilstandRepository.lagre(eldsteRegistrering);

        AktiveringTilstand nyesteRegistering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering2.getId())
                .opprettet(LocalDateTime.now().minusMinutes(1))
                .status(ARENA_OK)
                .build();
        aktiveringTilstandRepository.lagre(nyesteRegistering);

        AktiveringTilstand midtersteRegistering = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering3.getId())
                .opprettet(LocalDateTime.now().minusMinutes(3))
                .status(ARENA_OK)
                .build();
        aktiveringTilstandRepository.lagre(midtersteRegistering);

        Optional<AktiveringTilstand> lagretTilstand = aktiveringTilstandRepository.finnNesteAktiveringTilstandForOverforing();

        assertThat(lagretTilstand.isPresent()).isFalse();
    }

    @Test
    public void finnRegistreringTilstandMed_skal_returnere_alle_tilstander_med_angitt_status() {
        OrdinaerBrukerRegistrering lagretRegistrering1 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);

        AktiveringTilstand tilstand1 = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering1.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(MOTTATT)
                .build();
        aktiveringTilstandRepository.lagre(tilstand1);

        OrdinaerBrukerRegistrering lagretRegistrering2 = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);

        AktiveringTilstand tilstand2 = registreringTilstand()
                .brukerRegistreringId(lagretRegistrering2.getId())
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(ARENA_OK)
                .build();
        aktiveringTilstandRepository.lagre(tilstand2);

        List<AktiveringTilstand> mottatteRegistreringer = aktiveringTilstandRepository.finnAktiveringTilstandMed(MOTTATT);
        assertThat(mottatteRegistreringer).hasSize(1);
    }
}
