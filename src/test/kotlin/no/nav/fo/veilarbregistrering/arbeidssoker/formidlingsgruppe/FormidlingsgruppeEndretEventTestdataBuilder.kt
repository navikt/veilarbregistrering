package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

object FormidlingsgruppeEndretEventTestdataBuilder {

    fun formidlingsgruppeEndret(tidspunkt: LocalDateTime, formidlingsgruppe: String = "ARBS"): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
            Foedselsnummer("12345678910"),
            "012345",
            "AKTIV",
            Operation.INSERT,
            Formidlingsgruppe(formidlingsgruppe),
            tidspunkt,
            null,
            null
        )
    }

    fun formidlingsgruppeEndret(
        formidlingsgruppe: String = "ARBS",
        personId: String = "012345",
        personIdStatus: String = "AKTIV",
        tidspunkt: LocalDateTime): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
            Foedselsnummer("12345678910"),
            personId,
            personIdStatus,
            Operation.INSERT,
            Formidlingsgruppe(formidlingsgruppe),
            tidspunkt,
            null,
            null
        )
    }

    fun testEvent(test: LocalDateTime, operation: Operation): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
            Foedselsnummer("12345678910"),
            "012345",
            "AKTIV",
            operation,
            Formidlingsgruppe("ISERV"),
            test,
            Formidlingsgruppe("ARBS"),
            test.minusDays(1)
        )
    }
}