package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDateTime;

public class FormidlingsgruppeEvent {

    private final Foedselsnummer foedselsnummer;
    private final String person_id;
    private final Formidlingsgruppe formidlingsgruppe;
    private final LocalDateTime formidlingsgruppeEndret;

    public FormidlingsgruppeEvent(Foedselsnummer foedselsnummer, String person_id, Formidlingsgruppe formidlingsgruppe, LocalDateTime formidlingsgruppeEndret) {
        this.foedselsnummer = foedselsnummer;
        this.person_id = person_id;
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
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

    public LocalDateTime getFormidlingsgruppeEndret() {
        return formidlingsgruppeEndret;
    }

    @Override
    public String toString() {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + foedselsnummer.maskert() +
                ", person_id='" + person_id + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret.toString() +
                '}';
    }
}
