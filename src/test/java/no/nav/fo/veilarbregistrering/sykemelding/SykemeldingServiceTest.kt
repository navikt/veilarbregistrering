package no.nav.fo.veilarbregistrering.sykemelding

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SykemeldingServiceTest {
    private lateinit var sykemeldingService: SykemeldingService
    private lateinit var sykemeldingGateway: SykemeldingGateway

    @BeforeEach
    fun setup() {
        sykemeldingGateway = mockk()
        sykemeldingService = SykemeldingService(
            sykemeldingGateway,
            mockk(relaxed = true)
        )
    }

    @Test
    fun `hentSykmeldtInfoData skal håndtere maksdato lik null`() {
        every { sykemeldingGateway.hentReberegnetMaksdato(any()) } returns Maksdato.nullable()
        val sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(FNR)
        Assertions.assertThat(sykmeldtInfoData.getMaksDato()).isNull()
        Assertions.assertThat(sykmeldtInfoData.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv).isFalse
    }

    @Test
    fun `hentSykmeldtInfoData skal håndtere maksdato`() {
        val datoUtenforAktivInngang = LocalDate.now().plusWeeks(20)
        every { sykemeldingGateway.hentReberegnetMaksdato(any()) } returns Maksdato.of(datoUtenforAktivInngang.toString())
        val sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(FNR)
        Assertions.assertThat(sykmeldtInfoData.getMaksDato()).isEqualTo(datoUtenforAktivInngang.toString())
        Assertions.assertThat(sykmeldtInfoData.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv).isFalse
    }

    @Test
    fun `hentSykmeldtInfoData skal håndtere maksdato mellom 39 og 52 uker`() {
        val maksdato = LocalDate.now().plusWeeks(3).toString()
        every { sykemeldingGateway.hentReberegnetMaksdato(any()) } returns Maksdato.of(maksdato)

        val sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(FNR)
        Assertions.assertThat(sykmeldtInfoData.getMaksDato()).isEqualTo(maksdato)
        Assertions.assertThat(sykmeldtInfoData.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv).isTrue
    }

    companion object {
        private val FNR = Foedselsnummer.of("121212123123")
    }
}