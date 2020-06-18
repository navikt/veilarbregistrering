package no.nav.fo.veilarbregistrering.kafka;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

class FormidlingsgruppeMapper {

    static FormidlingsgruppeEvent map(String record) {
        Gson gson = new Gson();
        GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto = gson.fromJson(record, GgArenaFormidlinggruppeDto.class);
        return map(ggArenaFormidlinggruppeDto);
    }

    private static FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        AfterDto after = ggArenaFormidlinggruppeDto.getAfter();

        LocalDateTime localDateTime = ofNullable(after.getMOD_DATO())
                .map(LocalDateTime::parse)
                .orElse(LocalDateTime.MIN);

        return new FormidlingsgruppeEvent(
                Foedselsnummer.of(after.getFODSELSNR()),
                after.getPERSON_ID(),
                Formidlingsgruppe.of(after.getFORMIDLINGSGRUPPEKODE()),
                localDateTime);
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

        private AfterDto after;

        GgArenaFormidlinggruppeDto(AfterDto after) {
            this.after = after;
        }

        AfterDto getAfter() {
            return after;
        }

        void setAfter(AfterDto after) {
            this.after = after;
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
}
