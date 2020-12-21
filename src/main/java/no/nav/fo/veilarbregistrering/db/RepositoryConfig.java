package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class RepositoryConfig {

    @Bean
    BrukerRegistreringRepository brukerRegistreringRepository(NamedParameterJdbcTemplate db) {
        return new BrukerRegistreringRepositoryImpl(db);
    }

    @Bean
    RegistreringTilstandRepository registreringTilstandRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new RegistreringTilstandRepositoryImpl(namedParameterJdbcTemplate);
    }

    @Bean
    ArbeidssokerRepository arbeidssokerRepository(JdbcTemplate db) {
        return new ArbeidssokerRepositoryImpl(db);
    }

    @Bean
    OppgaveRepository oppgaveRepository(JdbcTemplate db) {
        return new OppgaveRepositoryImpl(db);
    }

    @Bean
    ProfileringRepository profileringRepository(JdbcTemplate db) {
        return new ProfileringRepositoryImpl(db);
    }

    @Bean
    ManuellRegistreringRepository manuellRegistreringRepository(JdbcTemplate db) {
        return new ManuellRegistreringRepositoryImpl(db);
    }
}
