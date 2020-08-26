package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Objects;

class ArbeidssokerperiodeRaaData {

    private final String formidlingsgruppe;
    private final int personId;
    private final String personIdStatus;
    private final Timestamp formidlingsgruppeEndret;

    public ArbeidssokerperiodeRaaData(String formidlingsgruppe, int personId, String personIdStatus, Timestamp formidlingsgruppeEndret) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.personId = personId;
        this.personIdStatus = personIdStatus;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
    }

    Timestamp getFormidlingsgruppeEndret() {
        return this.formidlingsgruppeEndret;
    }

    String getFormidlingsgruppe() {
        return this.formidlingsgruppe;
    }

    int getPersonId() {
        return personId;
    }

    String getPersonIdStatus() {
        return personIdStatus;
    }

    boolean erAktiv() {
        return Objects.equals(personIdStatus, "AKTIV");
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
