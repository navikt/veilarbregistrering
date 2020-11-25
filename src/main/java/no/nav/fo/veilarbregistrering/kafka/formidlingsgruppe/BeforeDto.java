package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

class BeforeDto {

    private String PERSON_ID;
    private String PERSON_ID_STATUS;
    private String FODSELSNR;
    private String FORMIDLINGSGRUPPEKODE;
    private String MOD_DATO;

    BeforeDto(String person_id, String person_id_status, String fodselsnr, String formidlingsgruppekode, String mod_dato) {
        this.PERSON_ID = person_id;
        this.PERSON_ID_STATUS = person_id_status;
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

    public String getPERSON_ID_STATUS() {
        return PERSON_ID_STATUS;
    }

    void setPERSON_ID_STATUS(String PERSON_ID_STATUS) {
        this.PERSON_ID_STATUS = PERSON_ID_STATUS;
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
