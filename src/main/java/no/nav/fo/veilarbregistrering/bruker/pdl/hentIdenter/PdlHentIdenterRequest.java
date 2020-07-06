package no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter;

public class PdlHentIdenterRequest {

    private String query;
    private HentIdenterVariables variables;

    public PdlHentIdenterRequest(String query, HentIdenterVariables variables) {
        this.query = query;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HentIdenterVariables getVariables() {
        return variables;
    }

    public void setVariables(HentIdenterVariables variables) {
        this.variables = variables;
    }
}
