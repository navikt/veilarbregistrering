package no.nav.fo.veilarbregistrering.oppgave

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer.KommuneMedBydel
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class OppgaveRouterTest {

    @Test
    fun `ingen arbeidsforhold skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { FlereArbeidsforhold.of(emptyList()) }
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway = arbeidsforholdGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `ingen enhet for orgnummer skal gi intern brukerstotte`() {
        val oppgaveRouter = oppgaveRouter()
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `ingen navenhet for organisasjon skal gi intern brukerstotte`() {
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1240"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `enhetsnummer skal returneres nar alle koblingen til arbeidsforhold er komplett`() {
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun `geografisk tilknytning med by med bydel skal gi intern brukerstotte`() {
        val personGateway = PersonGateway { Optional.of(GeografiskTilknytning.of("0301")) }

        val oppgaveRouter = oppgaveRouter(personGateway = personGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `geografisk tilknytning med unntak av landkode skal gi empty enhetsnummer`() {
        val personGateway = PersonGateway { Optional.of(GeografiskTilknytning.of("030106")) }

        val oppgaveRouter = oppgaveRouter(personGateway = personGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEmpty
    }

    @Test
    fun `geografisk tilknytning med landkode skal bruke arbeidsforhold til routing`() {
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val personGateway = PersonGateway { Optional.of(GeografiskTilknytning.of("DNK")) }

        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway, personGateway = personGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun `kommunenummer tilhorende kommune med bydeler skal tildeles intern brukerstotte`() {
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of(KommuneMedBydel.STAVANGER),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val personGateway = PersonGateway { Optional.of(GeografiskTilknytning.of("DNK")) }

        val oppgaveRouter = oppgaveRouter(enhetGateway = enhetGateway, personGateway = personGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `brukere med adressebeskyttelse FORTROLIG (kode 6) overlates til oppgave api`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.FORTROLIG)
        assertThat(enhetsnr).isEmpty
    }

    @Test
    fun `brukere med adressebeskyttelse STRENGT_FORTROLIG (kode 7) overlates til oppgave api`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        assertThat(enhetsnr).isEmpty
    }

    @Test
    fun `brukere med adressebeskyttelse STRENGT_FORTROLIG_UTLAND routes eksplisitt til spesialkontor`() {
        val enhetsnr = hentEnhetsnummerForBrukerMedAdressebeskyttelse(AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND)
        assertThat(enhetsnr).hasValue(Enhetnr.enhetForAdressebeskyttelse())
    }

    fun hentEnhetsnummerForBrukerMedAdressebeskyttelse(adressebeskyttelseGradering: AdressebeskyttelseGradering): Optional<Enhetnr> {
        val personGateway = PersonGateway { Optional.of(GeografiskTilknytning.of("0301")) }
        val person = Person.of(null, null, adressebeskyttelseGradering)
        val pdlOppslagGateway = StubPdlOppslagGateway(users = mapOf(BRUKER.aktorId to person))
        val oppgaveRouter = oppgaveRouter(personGateway = personGateway, pdlOppslagGateway = pdlOppslagGateway)

        return oppgaveRouter.hentEnhetsnummerFor(BRUKER)
    }

    private fun oppgaveRouter(
        arbeidsforholdGateway: ArbeidsforholdGateway = StubArbeidsforholdGateway(),
        enhetGateway: EnhetGateway = StubEnhetGateway(),
        norg2Gateway: Norg2Gateway = StubNorg2Gateway(),
        personGateway: PersonGateway = StubPersonGateway(),
        pdlOppslagGateway: PdlOppslagGateway = StubPdlOppslagGateway(),
        metricsService: MetricsService = mock()
    ) =
            OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, pdlOppslagGateway, metricsService)

    private class StubNorg2Gateway : Norg2Gateway {
        override fun hentEnhetFor(kommunenummer: Kommunenummer): Optional<Enhetnr> {
            if (Kommunenummer.of("1241") == kommunenummer) {
                return Optional.of(Enhetnr.of("232"))
            }
            return if (Kommunenummer.of(KommuneMedBydel.STAVANGER) == kommunenummer) {
                Optional.of(Enhetnr.of("1103"))
            } else Optional.empty()
        }

        override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = emptyMap()
    }

    private class StubArbeidsforholdGateway : ArbeidsforholdGateway {
        override fun hentArbeidsforhold(fnr: Foedselsnummer?) =
                FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert()
    }

    private class StubPersonGateway : PersonGateway {
        override fun hentGeografiskTilknytning(foedselsnummer: Foedselsnummer?) = Optional.empty<GeografiskTilknytning>()
    }

    private class StubEnhetGateway : EnhetGateway {
        override fun hentOrganisasjonsdetaljer(organisasjonsnummer: Organisasjonsnummer?) =
                Optional.empty<Organisasjonsdetaljer>()
    }

    private class StubPdlOppslagGateway(private val users: Map<AktorId, Person> = emptyMap()) : PdlOppslagGateway {
        override fun hentPerson(aktorId: AktorId?) = Optional.ofNullable(users[aktorId])

        override fun hentIdenter(fnr: Foedselsnummer?): Identer {
            TODO("Not yet implemented")
        }

        override fun hentIdenter(aktorId: AktorId?): Identer {
            TODO("Not yet implemented")
        }

    }

    private fun unleashServiceMedFeatures(vararg aktiverteFeatures: String) =
            mock<UnleashService>().also { unleashService ->
                aktiverteFeatures.forEach { aktivertFeature ->
                    whenever(unleashService.isEnabled(aktivertFeature)).thenReturn(true)
                }
            }

    companion object {
        private val BRUKER = Bruker.of(
                Foedselsnummer.of("12345678911"), AktorId.of("32235352"))
    }
}