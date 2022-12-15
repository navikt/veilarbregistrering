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
}