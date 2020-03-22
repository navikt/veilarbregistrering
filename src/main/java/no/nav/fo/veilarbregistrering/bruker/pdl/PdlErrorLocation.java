package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlErrorLocation {
    private Integer line;
    private Integer column;

    public PdlErrorLocation() {
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
