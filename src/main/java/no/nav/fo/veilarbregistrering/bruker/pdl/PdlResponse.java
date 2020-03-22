package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;

public class PdlResponse {
    private PdlHentPerson data;
    private List<PdlError> errors;

    public PdlResponse() {
    }

    public PdlHentPerson getData() {
        return data;
    }

    public void setData(PdlHentPerson data) {
        this.data = data;
    }

    public List<PdlError> getErrors() {
        return errors;
    }

    public void setErrors(List<PdlError> errors) {
        this.errors = errors;
    }
}
