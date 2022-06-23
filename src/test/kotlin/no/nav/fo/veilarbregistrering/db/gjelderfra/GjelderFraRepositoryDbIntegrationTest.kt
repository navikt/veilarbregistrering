package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class GjelderFraRepositoryDbIntegrationTest(
    @Autowired private val gjelderFraRepository: GjelderFraRepository,
    @Autowired private val brukerRegistreringRepository: BrukerRegistreringRepository,
) {

    @Test
    fun `skal returnere null når bruker ikke har registrert noen dato`() {
        val bruker = Bruker(Foedselsnummer("01234567890"), AktorId("1000010000100"))
        val gjelderFraDato = gjelderFraRepository.hentDatoFor(bruker)

        Assertions.assertEquals(null, gjelderFraDato)
    }

    @Test
    fun `skal lagre dato for bruker` () {
        val bruker = Bruker(Foedselsnummer("01234567890"), AktorId("1000010000100"))
        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            besvarelse = BesvarelseTestdataBuilder.gyldigBesvarelse(andreForhold = AndreForholdSvar.NEI)
        )
        val registreringsId = brukerRegistreringRepository.lagre(registrering, bruker).id

        gjelderFraRepository.opprettDatoFor(bruker, registreringsId, LocalDate.of(2022, 6, 23))

        val gjelderFraDato = gjelderFraRepository.hentDatoFor(bruker)

        Assertions.assertEquals(LocalDate.of(2022, 6, 23), gjelderFraDato?.dato)
    }

    @Test
    fun `returnerer nyeste lagrede dato for bruker`() {
        val bruker = Bruker(Foedselsnummer("01234567890"), AktorId("1000010000100"))
        val registrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            besvarelse = BesvarelseTestdataBuilder.gyldigBesvarelse(andreForhold = AndreForholdSvar.NEI)
        )

        val registreringsId = brukerRegistreringRepository.lagre(registrering, bruker).id

        gjelderFraRepository.opprettDatoFor(bruker, registreringsId, LocalDate.of(2022, 6, 23))
        gjelderFraRepository.opprettDatoFor(bruker, registreringsId, LocalDate.of(2022, 6, 22))
        gjelderFraRepository.opprettDatoFor(bruker, registreringsId, LocalDate.of(2022, 6, 21))

        val gjelderFraDato = gjelderFraRepository.hentDatoFor(bruker)

        Assertions.assertEquals(LocalDate.of(2022, 6, 21), gjelderFraDato?.dato)
    }

}
