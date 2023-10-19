package no.nav.fo.veilarbregistrering.profilering.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

data class ProfileringDto(val innsatsgruppe: String?, val servicegruppe: String?) {
    companion object {
        fun fra(triple: Triple<Innsatsgruppe?, Servicegruppe?, Formidlingsgruppe?>) = ProfileringDto(triple.first?.value(), triple.second?.kode)
    }
}
