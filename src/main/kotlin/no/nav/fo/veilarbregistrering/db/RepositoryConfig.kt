package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.db.aktorIdCache.AktorIdCacheRepositoryImpl
import no.nav.fo.veilarbregistrering.db.arbeidssoker.FormidlingsgruppeRepositoryImpl
import no.nav.fo.veilarbregistrering.db.arbeidssoker.MeldekortRepositoryImpl
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.registrering.*
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

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
    fun meldekortRepository(db: NamedParameterJdbcTemplate): MeldekortRepository {
        return MeldekortRepositoryImpl(db)
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
    fun aktorIdCacheRepository(db: NamedParameterJdbcTemplate): AktorIdCacheRepository {
        return AktorIdCacheRepositoryImpl(db)
    }
}
