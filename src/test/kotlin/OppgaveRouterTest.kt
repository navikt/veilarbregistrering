package no.nav.fo.veilarbregistrering.oppgave

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
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
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.sbl.featuretoggle.unleash.UnleashService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.util.*

class OppgaveRouterTest {

    @Test
    fun `ingen arbeidsforhold skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforhold.of(emptyList()) }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `ingen enhet for orgnummer skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `ingen navenhet for organisasjon skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1240"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun `enhetsnummer skal returneres nar alle koblingen til arbeidsforhold er komplett`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun `geografisk tilknytning med by med bydel skal gi intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("0301")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `geografisk tilknytning med unntak av landkode skal gi empty enhetsnummer`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("030106")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEmpty
    }

    @Test
    fun `geografisk tilknytning med landkode skal bruke arbeidsforhold til routing`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("DNK")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun `kommunenummer tilhorende kommune med bydeler skal tildeles intern brukerstotte`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of(KommuneMedBydel.STAVANGER),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("DNK")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun `brukere med adressebeskyttelse overlates til oppgave api`() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("030106")) }
        val unleashService = unleashServiceMedFeatures("veilarbregistrering.adressebeskyttelse")
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val person = Person.of(null, null, null, null, AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        whenever(pdlOppslagGateway.hentPerson(BRUKER.aktorId)).thenReturn(Optional.of(person))
        val oppgaveRouter = oppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        assertThat(enhetsnr).isEmpty
    }

    private fun oppgaveRouter(
            arbeidsforholdGateway: ArbeidsforholdGateway,
            enhetGateway: EnhetGateway,
            norg2Gateway: Norg2Gateway,
            personGateway: PersonGateway,
            unleashService: UnleashService,
            pdlOppslagGateway: PdlOppslagGateway
    ) =
            OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)

    internal class StubNorg2Gateway : Norg2Gateway {
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