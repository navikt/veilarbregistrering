package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

public class FormidlingsgruppeEvent {

    private final Foedselsnummer foedselsnummer;
    private final String person_id;
    private final Formidlingsgruppe formidlingsgruppe;

    public FormidlingsgruppeEvent(Foedselsnummer foedselsnummer, String person_id, Formidlingsgruppe formidlingsgruppe) {
        this.foedselsnummer = foedselsnummer;
        this.person_id = person_id;
        this.formidlingsgruppe = formidlingsgruppe;
    }

    public Foedselsnummer getFoedselsnummer() {
        return foedselsnummer;
    }

    public String getPerson_id() {
        return person_id;
    }

    public Formidlingsgruppe getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    @Override
    public String toString() {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + foedselsnummer.maskert() +
                ", person_id='" + person_id + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                '}';
    }
}
