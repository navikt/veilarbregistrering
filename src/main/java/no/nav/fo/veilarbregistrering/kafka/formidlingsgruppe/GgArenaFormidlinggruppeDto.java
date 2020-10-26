package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

/**
 * GgArenaFormidlingsgruppe representerer Json som publiseres på `gg-arena-formidlingsgruppe-v1`
 *
 * {
 *   "table": "SIAMO.PERSON",
 *   "op_type": "I",
 *   "op_ts": "2020-04-07 15:46:32.899550",
 *   "current_ts": "2020-04-07T15:51:42.974023",
 *   "pos": "***********001144391",
 *   "after": {
 *     "PERSON_ID": 13919,
 *     "FODSELSNR": "***********",
 *     "FORMIDLINGSGRUPPEKODE": "ISERV",
 *     "MOD_DATO":
 *   }
 * }
 */
class GgArenaFormidlinggruppeDto {

    private String op_type;
    private AfterDto after;
    private BeforeDto before;

    GgArenaFormidlinggruppeDto(String op_type, AfterDto after, BeforeDto before) {
        this.op_type = op_type;
        this.after = after;
        this.before = before;
    }

    String getOp_type() {
        return op_type;
    }

    AfterDto getAfter() {
        return after;
    }

    BeforeDto getBefore() {
        return before;
    }

    void setAfter(AfterDto after) {
        this.after = after;
    }

    void setBefore(BeforeDto before) {
        this.before = before;
    }

    void setOp_type(String op_type) {
        this.op_type = op_type;
    }
}
