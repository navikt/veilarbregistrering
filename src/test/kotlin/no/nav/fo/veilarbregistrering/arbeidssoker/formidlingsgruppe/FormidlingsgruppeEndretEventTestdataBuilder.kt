package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

object FormidlingsgruppeEndretEventTestdataBuilder {

    fun formidlingsgruppeEndret(tidspunkt: LocalDateTime): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
            Foedselsnummer("12345678910"),
            "012345",
            "AKTIV",
            Operation.INSERT,
            Formidlingsgruppe("ARBS"),
            tidspunkt,
            null,
            null
        )
    }
}