package no.nav.fo.veilarbregistrering.bruker;

import java.util.List;

public class Identer {
    private List<Ident> identer;

    public Identer(List<Ident> identer) {
        this.identer = identer;
    }

    public static Identer of(List<Ident> identer) {
        return new Identer(identer);
    }

    public List<Ident> getIdenter() {
        return identer;
    }
}
