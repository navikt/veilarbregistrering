package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType

data class StartRegistreringStatusDto(
    val maksDato: String? = null,
    val underOppfolging: Boolean = false,
    val erSykmeldtMedArbeidsgiver: Boolean = false,
    val jobbetSeksAvTolvSisteManeder: Boolean? = null,
    val registreringType: RegistreringType? = null,
    val formidlingsgruppe: String? = null,
    val servicegruppe: String? = null,
    val rettighetsgruppe: String? = null,
    val geografiskTilknytning: String? = null,
    val alder: Int = 0,
) {
    override fun toString(): String =
        "StartRegistreringStatusDto(maksDato=$maksDato, " +
                "underOppfolging=$underOppfolging, " +
                "erSykmeldtMedArbeidsgiver=$erSykmeldtMedArbeidsgiver, " +
                "jobbetSeksAvTolvSisteManeder=$jobbetSeksAvTolvSisteManeder, " +
                "registreringType=$registreringType, " +
                "formidlingsgruppe=$formidlingsgruppe, " +
                "servicegruppe=$servicegruppe, " +
                "rettighetsgruppe=$rettighetsgruppe, " +
                "geografiskTilknytning=$geografiskTilknytning, " +
                "alder=$alder)"

}