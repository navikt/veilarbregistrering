package no.nav.fo.veilarbregistrering.bruker;

import javax.ws.rs.NotFoundException;
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

    public Foedselsnummer finnGjeldeneFnr() {
        Ident gjeldendeFnr = identer.stream()
                .filter(ident -> ident.getGruppe() == Gruppe.FOLKEREGISTERIDENT)
                .filter(ident -> !ident.isHistorisk())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Kunne ikke finne et gjeldende fødselsnummer"));
        return Foedselsnummer.of(gjeldendeFnr.getIdent());
    }

    public AktorId finnGjeldendeAktorId() {
        Ident gjeldendeAktorId = identer.stream()
                .filter(ident -> ident.getGruppe() == Gruppe.AKTORID)
                .filter(ident -> !ident.isHistorisk())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Kunne ikke finne en gjeldende aktørId"));
        return AktorId.of(gjeldendeAktorId.getIdent());
    }
}
