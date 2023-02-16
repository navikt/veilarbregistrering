package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka

internal class AfterDto(
    val PERSON_ID: String,
    val PERSON_ID_STATUS: String,
    val FODSELSNR: String?,
    val FORMIDLINGSGRUPPEKODE: String,
    val MOD_DATO: String
)