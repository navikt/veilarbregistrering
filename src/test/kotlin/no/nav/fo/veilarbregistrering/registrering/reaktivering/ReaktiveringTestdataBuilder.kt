package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import java.time.LocalDateTime

object ReaktiveringTestdataBuilder {

    fun gyldigReaktivering(aktorId: AktorId): Reaktivering {
        return gyldigReaktivering(aktorId, LocalDateTime.now())
    }

    fun gyldigReaktivering(aktorId: AktorId, opprettetDato: LocalDateTime): Reaktivering {
        return Reaktivering(1, aktorId, opprettetDato)
    }

}