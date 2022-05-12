package no.nav.fo.veilarbregistrering.profilering.resources

import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

data class ProfileringDto(val innsatsgruppe: String?, val servicegruppe: String?) {
    companion object {
        fun fra(pair: Pair<Innsatsgruppe?, Servicegruppe?>) = ProfileringDto(pair.first?.value(), pair.second?.kode)
    }
}
