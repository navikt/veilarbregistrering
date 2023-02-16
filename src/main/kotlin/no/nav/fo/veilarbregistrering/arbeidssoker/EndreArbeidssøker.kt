package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import java.time.LocalDateTime

sealed interface EndreArbeidssøker {
    fun opprettetTidspunkt(): LocalDateTime
}

interface RegistrerArbeidssøker: EndreArbeidssøker

interface ReaktiverArbeidssøker: EndreArbeidssøker

interface FormidlingsgruppeEndret: EndreArbeidssøker {
    fun formidlingsgruppe(): Formidlingsgruppe
}