package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe

data class ArenaStatus(
    val servicegruppe: Servicegruppe,
    val rettighetsgruppe: Rettighetsgruppe,
    val formidlingsgruppe: Formidlingsgruppe
)
