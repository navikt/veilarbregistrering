package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

public class PdlHentPersonRequest {

    private String query;
    private HentPersonVariables variables;

    public PdlHentPersonRequest(String query, HentPersonVariables variables) {
        this.query = query;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HentPersonVariables getVariables() {
        return variables;
    }

    public void setVariables(HentPersonVariables variables) {
        this.variables = variables;
    }
}
