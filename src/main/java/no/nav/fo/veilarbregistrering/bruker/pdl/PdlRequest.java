package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlRequest {

    private String query;
    private Variables variables;

    public PdlRequest(String query, Variables variables) {
        this.query = query;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }
}
