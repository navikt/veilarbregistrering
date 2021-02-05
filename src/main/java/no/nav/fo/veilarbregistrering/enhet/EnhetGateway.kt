package no.nav.fo.veilarbregistrering.enhet

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import java.util.*

fun interface EnhetGateway {
    fun hentOrganisasjonsdetaljer(organisasjonsnummer: Organisasjonsnummer): Organisasjonsdetaljer?
}