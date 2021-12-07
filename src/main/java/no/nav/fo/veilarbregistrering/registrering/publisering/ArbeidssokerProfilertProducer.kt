package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import java.time.LocalDateTime

interface ArbeidssokerProfilertProducer {
    fun publiserProfilering(aktorId: AktorId?, innsatsgruppe: Innsatsgruppe?, profilertDato: LocalDateTime?)
}