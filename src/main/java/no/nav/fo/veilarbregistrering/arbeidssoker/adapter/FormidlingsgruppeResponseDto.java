package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class FormidlingsgruppeResponseDto {

    private String personId;
    private String fodselsnr;
    private List<FormidlingshistorikkDto> formidlingshistorikk;

    public FormidlingsgruppeResponseDto(
            String personId,
            String fodselsnr,
            List<FormidlingshistorikkDto> formidlingshistorikk) {
        this.personId = personId;
        this.fodselsnr = fodselsnr;
        this.formidlingshistorikk = formidlingshistorikk != null ? formidlingshistorikk : emptyList();
    }

    public String getPersonId() {
        return personId;
    }

    public String getFodselsnr() {
        return fodselsnr;
    }

    public List<FormidlingshistorikkDto> getFormidlingshistorikk() {
        return formidlingshistorikk;
    }
}
