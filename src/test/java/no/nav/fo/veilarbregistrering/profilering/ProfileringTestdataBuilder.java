package no.nav.fo.veilarbregistrering.profilering;

public class ProfileringTestdataBuilder {
    public static Profilering lagProfilering() {
        return new Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setAlder(62)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(false);
    }
}
