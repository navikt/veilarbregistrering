package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdata.medDato
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.function.Consumer

internal class ProfileringServiceTest {

    private lateinit var profileringService: ProfileringService
    private lateinit var arbeidsforholdGateway: ArbeidsforholdGateway

    @BeforeEach
    fun setUp() {
        arbeidsforholdGateway = StubArbeidsforholdGateway(mapOf(
            FOEDSELSNUMMER_MINUS_10_MND to FlereArbeidsforhold(listOf(
                medDato(LocalDate.now().minusMonths(10), LocalDate.now())
        )), FOEDSELSNUMMER_MINUS_2_MND to FlereArbeidsforhold(listOf(
                medDato(LocalDate.now().minusMonths(2), LocalDate.now())
        ))))
        profileringService = ProfileringService(arbeidsforholdGateway)
    }

    @Test
    fun `test profilering`() {
        val dagensDato = LocalDate.now()
        val aldre = listOf(25, 40, 65)
        val tilfredsstillerKravTilArbeidList = listOf(
            FOEDSELSNUMMER_MINUS_10_MND,
            FOEDSELSNUMMER_MINUS_2_MND
        )
        val alleMuligeBesvarelser = genererAlleMuligeBesvarelser()
        alleMuligeBesvarelser.forEach(Consumer { besvarelse: Besvarelse ->
            if (besvarelse.utdanning == null) {
                fail<Besvarelse>("Null utdanning: $besvarelse")
            }
            tilfredsstillerKravTilArbeidList.forEach(Consumer { tilfredsstillerKrav: Foedselsnummer ->
                aldre.forEach(
                    Consumer { alder: Int ->
                        val bruker = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
                            besvarelse = besvarelse
                        )
                        validerProfilering(
                            bruker,
                            alder,
                            tilfredsstillerKrav,
                            dagensDato, besvarelse
                        )
                    })
            })
        })
    }

    private fun genererAlleMuligeBesvarelser(): List<Besvarelse> {
        val besvarelser: MutableList<Besvarelse> = ArrayList()
        listOf(*DinSituasjonSvar.values()).forEach(Consumer { dinSituasjonSvar: DinSituasjonSvar? ->
            listOf(*SisteStillingSvar.values()).forEach(
                Consumer { sisteStillingSvar: SisteStillingSvar? ->
                    listOf(*UtdanningSvar.values()).forEach(
                        Consumer { utdanningSvar: UtdanningSvar? ->
                            listOf(*UtdanningGodkjentSvar.values()).forEach(
                                Consumer { utdanningGodkjentSvar: UtdanningGodkjentSvar? ->
                                    listOf(*UtdanningBestattSvar.values()).forEach(
                                        Consumer { utdanningBestattSvar: UtdanningBestattSvar? ->
                                            listOf(*HelseHinderSvar.values()).forEach(
                                                Consumer { helseHinderSvar: HelseHinderSvar? ->
                                                    listOf(*AndreForholdSvar.values()).forEach(
                                                        Consumer { andreForholdSvar: AndreForholdSvar? ->
                                                            besvarelser.add(
                                                                Besvarelse(
                                                                    dinSituasjon = dinSituasjonSvar,
                                                                    sisteStilling = sisteStillingSvar,
                                                                    utdanning = utdanningSvar,
                                                                    utdanningGodkjent = utdanningGodkjentSvar,
                                                                    utdanningBestatt = utdanningBestattSvar,
                                                                    helseHinder = helseHinderSvar,
                                                                    andreForhold = andreForholdSvar,
                                                                )
                                                            )
                                                        })
                                                })
                                        })
                                })
                        })
                })
        })
        return besvarelser
    }

    private fun validerProfilering(
        bruker: OrdinaerBrukerRegistrering,
        alder: Int,
        foedselsnummer: Foedselsnummer,
        dagensDato: LocalDate, besvarelse: Besvarelse
    ) {
        val innsatsgruppe = profileringService.profilerBruker(
            alder,
            foedselsnummer,
            besvarelse
        ).getInnsatsgruppe()
        val onsketInnsatsgruppe: Innsatsgruppe = if (besvarelse.helseHinder == HelseHinderSvar.JA || besvarelse.andreForhold == AndreForholdSvar.JA) {
                Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING
            } else if (alder in 18..59
                && arbeidsforholdGateway.hentArbeidsforhold(foedselsnummer)
                    .harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)
                && besvarelse.utdanning != UtdanningSvar.INGEN_UTDANNING
                && besvarelse.utdanningBestatt == UtdanningBestattSvar.JA && besvarelse.utdanningGodkjent == UtdanningGodkjentSvar.JA && besvarelse.helseHinder == HelseHinderSvar.NEI && besvarelse.andreForhold == AndreForholdSvar.NEI
            ) {
                Innsatsgruppe.STANDARD_INNSATS
            } else {
                Innsatsgruppe.SITUASJONSBESTEMT_INNSATS
            }
        assertEquals(onsketInnsatsgruppe, innsatsgruppe, "Feil profilering for bruker: $bruker")
    }

    @Test
    fun `test IKVAL-besvarelse mellom 30 og 59 år`() {
        val innsatsgruppe = profileringService.profilerBruker(
            35,
            FOEDSELSNUMMER_MINUS_10_MND,
            hentStandardInnsatsBesvarelse()
        ).getInnsatsgruppe()
        assertEquals(Innsatsgruppe.STANDARD_INNSATS, innsatsgruppe)
    }

    @Test
    fun `test BFORM-besvarelse over 59 år`() {
        val innsatsgruppe = profileringService.profilerBruker(
            60,
            FOEDSELSNUMMER_MINUS_10_MND,
            hentStandardInnsatsBesvarelse()
        ).getInnsatsgruppe()
        assertEquals(Innsatsgruppe.SITUASJONSBESTEMT_INNSATS, innsatsgruppe)
    }

    @Test
    fun `test BKART-besvarelse`() {
        val innsatsgruppe = profileringService.profilerBruker(
            40,
            FOEDSELSNUMMER_MINUS_10_MND,
            hentArbeidsEvneVurderingBesvarelse()
        ).getInnsatsgruppe()
        assertEquals(Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING, innsatsgruppe)
    }

    private fun hentStandardInnsatsBesvarelse(): Besvarelse =
        BesvarelseTestdataBuilder.gyldigBesvarelse()

    private fun hentArbeidsEvneVurderingBesvarelse(): Besvarelse =
        BesvarelseTestdataBuilder.gyldigBesvarelse(helseHinder = HelseHinderSvar.JA)

    companion object {
        private val FOEDSELSNUMMER_MINUS_10_MND = Foedselsnummer.of("12345678911")
        private val FOEDSELSNUMMER_MINUS_2_MND = Foedselsnummer.of("11987654321")
    }
}

private class StubArbeidsforholdGateway constructor(private val arbeidsforholdMap: Map<Foedselsnummer, FlereArbeidsforhold>) : ArbeidsforholdGateway {
    override fun hentArbeidsforhold(fnr: Foedselsnummer): FlereArbeidsforhold {
        return arbeidsforholdMap[fnr]!!
    }
}
