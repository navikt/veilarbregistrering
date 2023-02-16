package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka

/**
  GgArenaFormidlingsgruppe representerer Json som publiseres på `gg-arena-formidlingsgruppe-v1`

{
    "table": "SIAMO.PERSON",
    "op_type": "I",
    "op_ts": "2020-04-07 15:46:32.899550",
    "current_ts": "2020-04-07T15:51:42.974023",
    "pos": "***********001144391",
    "after": {
        "PERSON_ID": 13919,
        "FODSELSNR": "***********",
        "FORMIDLINGSGRUPPEKODE": "ISERV",
        "MOD_DATO":
    }
}
 */
internal data class GgArenaFormidlinggruppeDto(val op_type: String, val after: AfterDto?, val before: BeforeDto?)