package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlError;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse;

import java.util.List;

public class PdlHentPersonResponse implements PdlResponse {
    private PdlHentPerson data;
    private List<PdlError> errors;

    public PdlHentPersonResponse() {
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
