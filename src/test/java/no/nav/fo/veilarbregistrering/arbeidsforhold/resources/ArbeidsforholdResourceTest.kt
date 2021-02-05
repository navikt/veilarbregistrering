package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class ArbeidsforholdResourceTest {
    private lateinit var autorisasjonService: AutorisasjonService
    private lateinit var arbeidsforholdResource: ArbeidsforholdResource
    private lateinit var userService: UserService
    private lateinit var arbeidsforholdGateway: ArbeidsforholdGateway

    @BeforeEach
    fun setup() {
        autorisasjonService = mockk()
        every { autorisasjonService.sjekkLesetilgangTilBruker(any()) } returns true
        userService = mockk()
        arbeidsforholdGateway = mockk()
        arbeidsforholdResource = ArbeidsforholdResource(
            autorisasjonService,
            userService,
            arbeidsforholdGateway
        )
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvSisteArbeidsforhold() {
        every { userService.finnBrukerGjennomPdl() } returns Bruker.of(IDENT, AktorId.of("1234"))
        every { arbeidsforholdGateway.hentArbeidsforhold(IDENT) } returns flereArbeidsforhold()
        arbeidsforholdResource.hentSisteArbeidsforhold()
        verify(exactly = 1) { autorisasjonService.sjekkLesetilgangTilBruker(any()) }
    }

    private fun flereArbeidsforhold(): FlereArbeidsforhold {
        val fom3 = LocalDate.of(2017, 1, 1)
        val tom3 = LocalDate.of(2017, 11, 30)
        val fom2 = LocalDate.of(2017, 10, 1)
        val tom2 = LocalDate.of(2017, 11, 30)
        val fom1 = LocalDate.of(2017, 11, 1)
        val tom1 = LocalDate.of(2017, 11, 30)
        val sisteArbeidsforholdVarighet3 = ArbeidsforholdTestdataBuilder.medDato(fom3, tom3)
        val sisteArbeidsforholdvarighet2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2)
        val sisteArbeidsforholdVarighet1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1)
        return FlereArbeidsforhold.of(
            Arrays.asList(
                sisteArbeidsforholdVarighet1,
                sisteArbeidsforholdvarighet2,
                sisteArbeidsforholdVarighet3
            )
        )
    }

    companion object {
        private val IDENT = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
    }
}