package no.nav.fo.veilarbregistrering.db

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.db.registrering.ReaktiveringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.db.registrering.SykmeldtRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.db.arbeidssoker.FormidlingsgruppeRepositoryImpl
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.migrering.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraRepository
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {
    @Bean
    fun reaktiveringRepository(db: NamedParameterJdbcTemplate): ReaktiveringRepository {
        return ReaktiveringRepositoryImpl(db)
    }

    @Bean
    fun sykmeldtRegistreringRepository(db: NamedParameterJdbcTemplate): SykmeldtRegistreringRepository {
        return SykmeldtRegistreringRepositoryImpl(db)
    }

    @Bean
    fun brukerRegistreringRepository(db: NamedParameterJdbcTemplate): BrukerRegistreringRepository {
        return BrukerRegistreringRepositoryImpl(db)
    }

    @Bean
    fun registreringTilstandRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): RegistreringTilstandRepository {
        return RegistreringTilstandRepositoryImpl(namedParameterJdbcTemplate)
    }

    @Bean
    fun arbeidssokerRepository(db: NamedParameterJdbcTemplate): FormidlingsgruppeRepository {
        return FormidlingsgruppeRepositoryImpl(db)
    }

    @Bean
    fun oppgaveRepository(db: NamedParameterJdbcTemplate): OppgaveRepository {
        return OppgaveRepositoryImpl(db)
    }

    @Bean
    fun profileringRepository(db: NamedParameterJdbcTemplate): ProfileringRepository {
        return ProfileringRepositoryImpl(db)
    }

    @Bean
    fun manuellRegistreringRepository(db: NamedParameterJdbcTemplate): ManuellRegistreringRepository {
        return ManuellRegistreringRepositoryImpl(db)
    }

    @Bean
    fun migreringRepository(db: NamedParameterJdbcTemplate): MigreringRepositoryImpl {
        return MigreringRepositoryImpl(db)
    }

    @Bean
    fun gjelderFraDato(db: NamedParameterJdbcTemplate): GjelderFraRepository {
        return GjelderFraRepositoryImpl(db)
    }
}
