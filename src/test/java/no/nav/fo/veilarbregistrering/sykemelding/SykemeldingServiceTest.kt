package no.nav.fo.veilarbregistrering.sykemelding

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate

class SykemeldingServiceTest {
    private lateinit var sykemeldingService: SykemeldingService
    private lateinit var sykemeldingGateway: SykemeldingGateway

    @BeforeEach
    fun setup() {
        sykemeldingGateway = mockk()
        sykemeldingService = SykemeldingService(
            sykemeldingGateway,
            mockk(relaxed = true),
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

        every { sykemeldingGateway.hentReberegnetMaksdato(any()) } returns Maksdato.of("2021-11-01")
        val sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(FNR)
        Assertions.assertThat(sykmeldtInfoData.getMaksDato()).isEqualTo("2021-11-01")
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