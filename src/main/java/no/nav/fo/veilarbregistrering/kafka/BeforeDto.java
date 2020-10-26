package no.nav.fo.veilarbregistrering.kafka;

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
