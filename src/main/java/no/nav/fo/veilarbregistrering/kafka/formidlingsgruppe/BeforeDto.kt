package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

internal data class BeforeDto(
    val PERSON_ID: String,
    val PERSON_ID_STATUS: String,
    val FODSELSNR: String?,
    val FORMIDLINGSGRUPPEKODE: String,
    val MOD_DATO: String
)