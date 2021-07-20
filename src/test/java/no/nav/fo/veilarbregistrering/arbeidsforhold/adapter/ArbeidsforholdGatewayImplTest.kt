package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArbeidsforholdGatewayImplTest {

    private val arbeidsforholdGateway: ArbeidsforholdGateway = ArbeidsforholdGatewayImpl(StubAaregRestClient())

    @Test
    fun `hent arbeidsforhold fra aareg via rest`() {
        val flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"))
        assertThat(flereArbeidsforhold).isEqualTo(FlereArbeidsforholdTestdataBuilder.somJson())
    }
}
