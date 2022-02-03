package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.Companion.of
import java.time.LocalDate

class ProfileringService(private val arbeidsforholdGateway: ArbeidsforholdGateway) {

    fun profilerBruker(
        alder: Int,
        fnr: Foedselsnummer,
        besvarelse: Besvarelse
    ): Profilering {
        val flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(fnr)
        val harJobbetSammenhengendeSeksAvTolvSisteManeder =
            flereArbeidsforhold.harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato())
        val innsatsgruppe = of(besvarelse, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder)
        return Profilering(innsatsgruppe, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder)
    }

    protected fun dagensDato(): LocalDate {
        return LocalDate.now()
    }
}