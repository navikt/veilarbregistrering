package no.nav.fo.veilarbregistrering.oppgave

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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.util.*

class OppgaveRouterTest {
    @Test
    fun ingen_arbeidsforhold_skal_gi_intern_brukerstotte() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforhold.of(emptyList()) }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun ingen_enhet_for_orgnummer_skal_gi_intern_brukerstotte() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun ingen_navenhet_for_organisasjon_skal_gi_intern_brukerstotte() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1240"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.of("2930"))
    }

    @Test
    fun enhetsnummer_skal_returneres_nar_alle_koblingen_til_arbeidsforhold_er_komplett() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.empty() }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun geografisk_tilknytning_med_by_med_bydel_skal_gi_intern_brukerstotte() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("0301")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun geografisk_tilknytning_med_unntak_av_landkode_skal_gi_empty_enhetsnummer() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("030106")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).isEmpty
    }

    @Test
    fun geografisk_tilknytning_med_landkode_skal_bruke_arbeidsforhold_til_routing() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of("1241"),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("DNK")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.of("232"))
    }

    @Test
    fun kommunenummer_tilhorende_kommune_med_bydeler_skal_tildeles_intern_brukerstotte() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val forretningsadresse = Forretningsadresse(
                Kommunenummer.of(KommuneMedBydel.STAVANGER),
                Periode.of(LocalDate.of(2020, 1, 1), null))
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.of(Organisasjonsdetaljer.of(listOf(forretningsadresse), emptyList())) }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("DNK")) }
        val unleashService = Mockito.mock(UnleashService::class.java)
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).hasValue(Enhetnr.internBrukerstotte())
    }

    @Test
    fun brukere_med_adressebeskyttelse_overlates_til_oppgave_api() {
        val arbeidsforholdGateway = ArbeidsforholdGateway { fnr: Foedselsnummer? -> FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert() }
        val enhetGateway = EnhetGateway { organisasjonsnummer: Organisasjonsnummer? -> Optional.empty() }
        val norg2Gateway: Norg2Gateway = StubNorg2Gateway()
        val personGateway = PersonGateway { foedselsnummer: Foedselsnummer? -> Optional.of(GeografiskTilknytning.of("030106")) }
        val unleashService = unleashServiceMedFeatures("veilarbregistrering.adressebeskyttelse")
        val pdlOppslagGateway = Mockito.mock(PdlOppslagGateway::class.java)
        val person = Person.of(null, null, null, null, AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        Mockito.`when`(pdlOppslagGateway.hentPerson(BRUKER.aktorId)).thenReturn(Optional.of(person))
        val oppgaveRouter = OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(BRUKER)
        Assertions.assertThat(enhetsnr).isEmpty
    }

    internal class StubNorg2Gateway : Norg2Gateway {
        override fun hentEnhetFor(kommunenummer: Kommunenummer): Optional<Enhetnr> {
            if (Kommunenummer.of("1241") == kommunenummer) {
                return Optional.of(Enhetnr.of("232"))
            }
            return if (Kommunenummer.of(KommuneMedBydel.STAVANGER) == kommunenummer) {
                Optional.of(Enhetnr.of("1103"))
            } else Optional.empty()
        }

        override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> {
            return emptyMap()
        }
    }

    private fun unleashServiceMedFeatures(vararg aktiverteFeatures: String): UnleashService {
        val unleashService = Mockito.mock(UnleashService::class.java)
        for (aktivertFeature in aktiverteFeatures) {
            Mockito.`when`(unleashService.isEnabled(aktivertFeature)).thenReturn(true)
        }
        return unleashService
    }

    companion object {
        private val BRUKER = Bruker.of(
                Foedselsnummer.of("12345678911"), AktorId.of("32235352"))
    }
}