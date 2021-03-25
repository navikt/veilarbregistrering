package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.*;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.ReaktiveringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class RepositoryConfig {

    @Bean
    ReaktiveringRepository reaktiveringRepository(NamedParameterJdbcTemplate db) {
        return new ReaktiveringRepositoryImpl(db);
    }

    @Bean
    SykmeldtRegistreringRepository sykmeldtRegistreringRepository(NamedParameterJdbcTemplate db) {
        return new SykmeldtRegistreringRepositoryImpl(db);
    }

    @Bean
    BrukerRegistreringRepository brukerRegistreringRepository(NamedParameterJdbcTemplate db) {
        return new BrukerRegistreringRepositoryImpl(db);
    }

    @Bean
    RegistreringTilstandRepository registreringTilstandRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new RegistreringTilstandRepositoryImpl(namedParameterJdbcTemplate);
    }

    @Bean
    ArbeidssokerRepository arbeidssokerRepository(NamedParameterJdbcTemplate db) {
        return new ArbeidssokerRepositoryImpl(db);
    }

    @Bean
    OppgaveRepository oppgaveRepository(NamedParameterJdbcTemplate db) {
        return new OppgaveRepositoryImpl(db);
    }

    @Bean
    ProfileringRepository profileringRepository(NamedParameterJdbcTemplate db) {
        return new ProfileringRepositoryImpl(db);
    }

    @Bean
    ManuellRegistreringRepository manuellRegistreringRepository(NamedParameterJdbcTemplate db) {
        return new ManuellRegistreringRepositoryImpl(db);
    }
}
