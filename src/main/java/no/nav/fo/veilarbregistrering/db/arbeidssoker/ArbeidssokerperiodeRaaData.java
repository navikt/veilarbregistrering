package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import java.sql.Timestamp;
import java.util.Comparator;

class ArbeidssokerperiodeRaaData {

    private final String formidlingsgruppe;
    private final Timestamp formidlingsgruppeEndret;

    ArbeidssokerperiodeRaaData(String formidlingsgruppe, Timestamp formidlingsgruppeEndret) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
    }

    Timestamp getFormidlingsgruppeEndret() {
        return this.formidlingsgruppeEndret;
    }

    String getFormidlingsgruppe() {
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
