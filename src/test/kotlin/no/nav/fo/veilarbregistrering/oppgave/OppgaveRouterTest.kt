package no.nav.fo.veilarbregistrering.oppgave

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.enhet.Kommune.KommuneMedBydel.STAVANGER
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OppgaveRouterTest {

    @Test
    fun `ingen arbeidsforhold skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = object : ArbeidsforholdGateway {
            override fun hentArbeidsforhold(fnr: Foedselsnummer): FlereArbeidsforhold {
                return FlereArbeidsforhold(emptyList())
            }
        }
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway = arbeidsforholdGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr("2930"))
    }

    @Test
    fun `ingen enhet for orgnummer skal gi intern brukerstotte`() {
        val oppgaveRouter = oppgaveRouter()
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr("2930"))
    }

    @Test
    fun `ingen navenhet for organisasjon skal gi intern brukerstotte`() {
        val forretningsadresse = Forretningsadresse(
                Kommune("1240"),
                Periode(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Organisasjonsdetaljer(listOf(forretningsadresse), emptyList()) }

        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr("2930"))
    }

    @Test
    fun `enhetsnummer skal returneres nar alle koblingen til arbeidsforhold er komplett`() {
        val forretningsadresse = Forretningsadresse(
                Kommune("1241"),
                Periode(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Organisasjonsdetaljer(listOf(forretningsadresse), emptyList()) }
        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr("232"))
    }

    @Test
    fun `geografisk tilknytning med by med bydel skal gi intern brukerstotte`() {
        val pdlOppslagGateway = StubPdlOppslagGateway(geografiskTilknytning = GeografiskTilknytning("0301"))
        val oppgaveRouter = oppgaveRouter(pdlOppslagGateway = pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `geografisk tilknytning med unntak av landkode skal gi empty enhetsnummer`() {
        val pdlOppslagGateway = StubPdlOppslagGateway(geografiskTilknytning = GeografiskTilknytning("030106"))

        val oppgaveRouter = oppgaveRouter(pdlOppslagGateway = pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isNull()
    }

    @Test
    fun `geografisk tilknytning med landkode skal bruke arbeidsforhold til routing`() {
        val forretningsadresse = Forretningsadresse(
                Kommune("1241"),
                Periode(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Organisasjonsdetaljer(listOf(forretningsadresse), emptyList()) }
        val pdlOppslagGateway = StubPdlOppslagGateway(geografiskTilknytning = GeografiskTilknytning("DNK"))

        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway, pdlOppslagGateway = pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr("232"))
    }

    @Test
    fun `kommunenummer tilhorende kommune med bydeler skal tildeles intern brukerstotte`() {
        val forretningsadresse = Forretningsadresse(
                Kommune.medBydel(STAVANGER),
                Periode(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Organisasjonsdetaljer(listOf(forretningsadresse), emptyList()) }
        val pdlOppslagGateway = StubPdlOppslagGateway(geografiskTilknytning = GeografiskTilknytning("DNK"))

        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway, pdlOppslagGateway = pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEqualTo(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `brukere med adressebeskyttelse FORTROLIG (kode 6) overlates til oppgave api`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.FORTROLIG)
        assertThat(enhetsnr).isNull()
    }

    @Test
    fun `brukere med adressebeskyttelse STRENGT_FORTROLIG (kode 7) overlates til oppgave api`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        assertThat(enhetsnr).isNull()
    }

    @Test
    fun `brukere med adressebeskyttelse STRENGT_FORTROLIG_UTLAND routes eksplisitt til spesialkontor`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND)
        assertThat(enhetsnr).isEqualTo(Enhetnr.enhetForAdressebeskyttelse())
    }

    private fun hentEnhetsnummerForBrukerMedAdressebeskyttelse(adressebeskyttelseGradering: AdressebeskyttelseGradering): Enhetnr? {
        val person = Person(null, null, adressebeskyttelseGradering, testNavn)
        val pdlOppslagGateway = StubPdlOppslagGateway(geografiskTilknytning = GeografiskTilknytning("0301"),
                                                      users = mapOf(BRUKER.aktorId to person))
        val oppgaveRouter = oppgaveRouter(pdlOppslagGateway = pdlOppslagGateway)

        return oppgaveRouter.hentEnhetsnummerFor(BRUKER)
    }

    private fun oppgaveRouter(
        arbeidsforholdGateway: ArbeidsforholdGateway = StubArbeidsforholdGateway(),
        enhetGateway: EnhetGateway = EnhetGateway { null },
        norg2Gateway: Norg2Gateway = StubNorg2Gateway(),
        pdlOppslagGateway: PdlOppslagGateway = StubPdlOppslagGateway(),
        metricsService: MetricsService = mockk(relaxed = true)
    ) =
            OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, pdlOppslagGateway, metricsService)

    private class StubNorg2Gateway : Norg2Gateway {
        override fun hentEnhetFor(kommune: Kommune): Enhetnr? {
            if (Kommune("1241") == kommune) {
                return Enhetnr("232")
            }
            return if (Kommune.medBydel(STAVANGER) == kommune) {
                Enhetnr("1103")
            } else null
        }

        override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = emptyMap()
    }

    private class StubArbeidsforholdGateway : ArbeidsforholdGateway {
        override fun hentArbeidsforhold(fnr: Foedselsnummer) =
            FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert()
    }

    private class StubPdlOppslagGateway(private val geografiskTilknytning: GeografiskTilknytning? = null ,private val users: Map<AktorId, Person> = emptyMap()) : PdlOppslagGateway {
        override fun hentPerson(aktorid: AktorId) = users[aktorid]

        override fun hentIdenter(fnr: Foedselsnummer): Identer {
            TODO("Not yet implemented")
        }

        override fun hentIdenter(aktorId: AktorId): Identer {
            TODO("Not yet implemented")
        }

        override fun hentIdenterBolk(aktorIder: List<AktorId>): Map<AktorId, Foedselsnummer> {
            TODO("Not yet implemented")
        }

        override fun hentGeografiskTilknytning(aktorId: AktorId) = geografiskTilknytning

    }

    companion object {
        private val BRUKER = Bruker(
                Foedselsnummer("12345678911"), AktorId("32235352"))
    }
}
