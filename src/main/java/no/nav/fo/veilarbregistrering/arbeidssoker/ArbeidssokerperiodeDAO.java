package no.nav.fo.veilarbregistrering.arbeidssoker;

import java.sql.Timestamp;
import java.util.Comparator;

public class ArbeidssokerperiodeDAO {

    private final String formidlingsgruppe;
    private final Timestamp formidlingsgruppeEndret;

    public ArbeidssokerperiodeDAO(String formidlingsgruppe, Timestamp formidlingsgruppeEndret) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
    }

    public Timestamp getFormidlingsgruppeEndret() {
        return this.formidlingsgruppeEndret;
    }

    public String getFormidlingsgruppe() {
        return this.formidlingsgruppe;
    }

    static class NyesteFoerst implements Comparator<ArbeidssokerperiodeDAO> {

        static ArbeidssokerperiodeDAO.NyesteFoerst nyesteFoerst() {
            return new ArbeidssokerperiodeDAO.NyesteFoerst();
        }

        @Override
        public int compare(ArbeidssokerperiodeDAO t0, ArbeidssokerperiodeDAO t1) {
            return t0.getFormidlingsgruppeEndret().compareTo(t1.getFormidlingsgruppeEndret());
        }

    }

}
