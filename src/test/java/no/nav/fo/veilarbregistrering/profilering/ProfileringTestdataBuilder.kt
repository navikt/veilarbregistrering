package no.nav.fo.veilarbregistrering.profilering

object ProfileringTestdataBuilder {
    fun lagProfilering(): Profilering {
        return Profilering()
            .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
            .setAlder(62)
            .setJobbetSammenhengendeSeksAvTolvSisteManeder(false)
    }
}
