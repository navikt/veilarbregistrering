package no.nav.fo.veilarbregistrering.config.db

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.aktorIdCache.db.AktorIdCacheRepositoryImpl
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.db.FormidlingsgruppeRepositoryImpl
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.db.MeldekortRepositoryImpl
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperiodeRepositoryImpl
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.oppgave.db.OppgaveRepositoryImpl
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.db.ProfileringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.db.RegistreringTilstandRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.db.BrukerRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.db.ReaktiveringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.db.SykmeldtRegistreringRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.veileder.db.ManuellRegistreringRepositoryImpl
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

    @Bean
    fun arbeidssokerperiodeRepository(db: NamedParameterJdbcTemplate): ArbeidssokerperiodeRepository {
        return ArbeidssokerperiodeRepositoryImpl(db)
    }
}
