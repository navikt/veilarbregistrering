package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate

class GjelderFraRepositoryImpl(private val db: NamedParameterJdbcTemplate): GjelderFraRepository {
    override fun opprettDatoFor(
        bruker: Bruker,
        brukerRegistrering: BrukerRegistrering,
        dato: LocalDate
    ): GjelderFraDato {
        TODO("Not yet implemented")
        throw NotImplementedError()
    }

    override fun hentDatoFor(bruker: Bruker): GjelderFraDato {
        TODO("Not yet implemented")
        throw NotImplementedError()
    }
}
