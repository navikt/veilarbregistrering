package no.nav.fo.veilarbregistrering.arbeidssoker;

import java.sql.Timestamp;
import java.util.Comparator;

public class ArbeidssokerperiodeRaaData {

    private final String formidlingsgruppe;
    private final Timestamp formidlingsgruppeEndret;

    public ArbeidssokerperiodeRaaData(String formidlingsgruppe, Timestamp formidlingsgruppeEndret) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
    }

    public Timestamp getFormidlingsgruppeEndret() {
        return this.formidlingsgruppeEndret;
    }

    public String getFormidlingsgruppe() {
        return this.formidlingsgruppe;
    }

    static class NyesteFoerst implements Comparator<ArbeidssokerperiodeRaaData> {

        static ArbeidssokerperiodeRaaData.NyesteFoerst nyesteFoerst() {
            return new ArbeidssokerperiodeRaaData.NyesteFoerst();
        }

        @Override
        public int compare(ArbeidssokerperiodeRaaData t0, ArbeidssokerperiodeRaaData t1) {
            return t1.getFormidlingsgruppeEndret().compareTo(t0.getFormidlingsgruppeEndret());
        }

    }

}
