package no.nav.fo.veilarbregistrering.kafka;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;

class FormidlingsgruppeMapper {

    private static final Gson GSON = new Gson();

    static FormidlingsgruppeEvent map(String record) {
        GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto = GSON.fromJson(record, GgArenaFormidlinggruppeDto.class);
        return map(ggArenaFormidlinggruppeDto);
    }

    private static FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        AfterDto after = ggArenaFormidlinggruppeDto.getAfter();

        Foedselsnummer foedselsnummer = ofNullable(after.getFODSELSNR())
                .map(Foedselsnummer::of)
                .orElse(null);

        BeforeDto before = ggArenaFormidlinggruppeDto.getBefore();

        return new FormidlingsgruppeEvent(
                foedselsnummer,
                after.getPERSON_ID(),
                mapOperation(ggArenaFormidlinggruppeDto.getOp_type()),
                Formidlingsgruppe.of(after.getFORMIDLINGSGRUPPEKODE()),
                modDato(after.getMOD_DATO()),
                before != null ? Formidlingsgruppe.of(before.getFORMIDLINGSGRUPPEKODE()) : null,
                before != null ? modDato(before.getMOD_DATO()) : null);
    }

    private static Operation mapOperation(String operation) {
        switch (operation) {
            case "I" : return Operation.INSERT;
            case "U" : return Operation.UPDATE;
            case "D" : return Operation.DELETE;
            default: throw new IllegalArgumentException("Ukjent op_type-verdi på Kafka: " + operation);
        }
    }

    private static LocalDateTime modDato(String mod_dato) {
        return ofNullable(mod_dato)
                .map(d -> LocalDateTime.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

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

    class AfterDto {

        private String PERSON_ID;
        private String FODSELSNR;
        private String FORMIDLINGSGRUPPEKODE;
        private String MOD_DATO;

        AfterDto(String person_id, String fodselsnr, String formidlingsgruppekode, String mod_dato) {
            this.PERSON_ID = person_id;
            this.FODSELSNR = fodselsnr;
            this.FORMIDLINGSGRUPPEKODE = formidlingsgruppekode;
            this.MOD_DATO = mod_dato;
        }

        String getPERSON_ID() {
            return PERSON_ID;
        }

        void setPERSON_ID(String PERSON_ID) {
            this.PERSON_ID = PERSON_ID;
        }

        String getFODSELSNR() {
            return FODSELSNR;
        }

        void setFODSELSNR(String FODSELSNR) {
            this.FODSELSNR = FODSELSNR;
        }

        String getFORMIDLINGSGRUPPEKODE() {
            return FORMIDLINGSGRUPPEKODE;
        }

        void setFORMIDLINGSGRUPPEKODE(String FORMIDLINGSGRUPPEKODE) {
            this.FORMIDLINGSGRUPPEKODE = FORMIDLINGSGRUPPEKODE;
        }

        String getMOD_DATO() {
            return MOD_DATO;
        }

        void setMOD_DATO(String MOD_DATO) {
            this.MOD_DATO = MOD_DATO;
        }
    }

    class BeforeDto {

        private String PERSON_ID;
        private String FODSELSNR;
        private String FORMIDLINGSGRUPPEKODE;
        private String MOD_DATO;

        BeforeDto(String person_id, String fodselsnr, String formidlingsgruppekode, String mod_dato) {
            this.PERSON_ID = person_id;
            this.FODSELSNR = fodselsnr;
            this.FORMIDLINGSGRUPPEKODE = formidlingsgruppekode;
            this.MOD_DATO = mod_dato;
        }

        String getPERSON_ID() {
            return PERSON_ID;
        }

        void setPERSON_ID(String PERSON_ID) {
            this.PERSON_ID = PERSON_ID;
        }

        String getFODSELSNR() {
            return FODSELSNR;
        }

        void setFODSELSNR(String FODSELSNR) {
            this.FODSELSNR = FODSELSNR;
        }

        String getFORMIDLINGSGRUPPEKODE() {
            return FORMIDLINGSGRUPPEKODE;
        }

        void setFORMIDLINGSGRUPPEKODE(String FORMIDLINGSGRUPPEKODE) {
            this.FORMIDLINGSGRUPPEKODE = FORMIDLINGSGRUPPEKODE;
        }

        String getMOD_DATO() {
            return MOD_DATO;
        }

        void setMOD_DATO(String MOD_DATO) {
            this.MOD_DATO = MOD_DATO;
        }
    }
}
