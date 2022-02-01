package no.nav.fo.veilarbregistrering.profilering

data class Profilering(
    var innsatsgruppe: Innsatsgruppe,
    var alder: Int,
    var jobbetSammenhengendeSeksAvTolvSisteManeder: Boolean,
) {

    override fun toString(): String {
        return "Profilering(jobbetSammenhengendeSeksAvTolvSisteManeder=$jobbetSammenhengendeSeksAvTolvSisteManeder, alder=$alder, innsatsgruppe=$innsatsgruppe)"
    }
}