package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.STANDARD_INNSATS

object ProfileringTestdataBuilder {
    fun lagProfilering(): Profilering = Profilering(STANDARD_INNSATS, 62, false)
}
