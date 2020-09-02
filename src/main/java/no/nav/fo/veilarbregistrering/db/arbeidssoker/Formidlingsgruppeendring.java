package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Objects;

class Formidlingsgruppeendring {

    private final String formidlingsgruppe;
    private final int personId;
    private final String personIdStatus;
    private final Timestamp formidlingsgruppeEndret;

    public Formidlingsgruppeendring(String formidlingsgruppe, int personId, String personIdStatus, Timestamp formidlingsgruppeEndret) {
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

    boolean erARBS() {
        return Objects.equals(formidlingsgruppe, "ARBS");
    }

    boolean erISERV() {
        return Objects.equals(formidlingsgruppe, "ISERV");
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

    @Override
    public String toString() {
        return "Formidlingsgruppeendring{" +
                "formidlingsgruppe='" + formidlingsgruppe + '\'' +
                ", personId=" + personId +
                ", personIdStatus='" + personIdStatus + '\'' +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret +
                '}';
    }

    static class NyesteFoerst implements Comparator<Formidlingsgruppeendring> {

        static Formidlingsgruppeendring.NyesteFoerst nyesteFoerst() {
            return new Formidlingsgruppeendring.NyesteFoerst();
        }

        @Override
        public int compare(Formidlingsgruppeendring t0, Formidlingsgruppeendring t1) {
            return t1.getFormidlingsgruppeEndret().compareTo(t0.getFormidlingsgruppeEndret());
        }

    }
}
