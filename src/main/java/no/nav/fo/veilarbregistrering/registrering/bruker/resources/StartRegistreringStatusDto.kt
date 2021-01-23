package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType

class StartRegistreringStatusDto {
    var maksDato: String? = null
        private set
    var isUnderOppfolging = false
        private set
    var isErSykmeldtMedArbeidsgiver = false
        private set
    var jobbetSeksAvTolvSisteManeder: Boolean? = null
        private set
    var registreringType: RegistreringType? = null
        private set
    var formidlingsgruppe: String? = null
        private set
    var servicegruppe: String? = null
        private set
    var rettighetsgruppe: String? = null
        private set
    var geografiskTilknytning: String? = null
        private set
    var alder = 0
        private set

    fun setMaksDato(maksDato: String?): StartRegistreringStatusDto {
        this.maksDato = maksDato
        return this
    }

    fun setUnderOppfolging(underOppfolging: Boolean): StartRegistreringStatusDto =
        this.apply { this.isUnderOppfolging = underOppfolging }

    fun setErSykmeldtMedArbeidsgiver(erSykmeldtMedArbeidsgiver: Boolean): StartRegistreringStatusDto =
        this.apply { this.isErSykmeldtMedArbeidsgiver = erSykmeldtMedArbeidsgiver }

    fun setJobbetSeksAvTolvSisteManeder(jobbetSeksAvTolvSisteManeder: Boolean?): StartRegistreringStatusDto =
        this.apply { this.jobbetSeksAvTolvSisteManeder = jobbetSeksAvTolvSisteManeder }

    fun setRegistreringType(registreringType: RegistreringType?): StartRegistreringStatusDto =
        this.apply { this.registreringType = registreringType }

    fun setFormidlingsgruppe(formidlingsgruppe: String?): StartRegistreringStatusDto =
        this.apply { this.formidlingsgruppe = formidlingsgruppe }

    fun setServicegruppe(servicegruppe: String?): StartRegistreringStatusDto =
        this.apply { this.servicegruppe = servicegruppe }

    fun setRettighetsgruppe(rettighetsgruppe: String?): StartRegistreringStatusDto =
        this.apply { this.rettighetsgruppe = rettighetsgruppe }

    fun setGeografiskTilknytning(geografiskTilknytning: String?): StartRegistreringStatusDto =
        this.apply { this.geografiskTilknytning = geografiskTilknytning }

    fun setAlder(alder: Int): StartRegistreringStatusDto =
        this.apply { this.alder = alder }

    override fun toString(): String =
        "StartRegistreringStatusDto(maksDato=$maksDato, " +
                "underOppfolging=$isUnderOppfolging, " +
                "erSykmeldtMedArbeidsgiver=$isErSykmeldtMedArbeidsgiver, " +
                "jobbetSeksAvTolvSisteManeder=$jobbetSeksAvTolvSisteManeder, " +
                "registreringType=$registreringType, " +
                "formidlingsgruppe=$formidlingsgruppe, " +
                "servicegruppe=$servicegruppe, " +
                "rettighetsgruppe=$rettighetsgruppe, " +
                "geografiskTilknytning=$geografiskTilknytning, " +
                "alder=$alder)"

}