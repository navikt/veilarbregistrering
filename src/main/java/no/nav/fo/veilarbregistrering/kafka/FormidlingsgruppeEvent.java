package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDateTime;
import java.util.Optional;

public class FormidlingsgruppeEvent implements EndretFormidlingsgruppeCommand {

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

    @Override
    public Optional<Foedselsnummer> getFoedselsnummer() {
        return Optional.ofNullable(foedselsnummer);
    }

    @Override
    public String getPerson_id() {
        return person_id;
    }

    @Override
    public Formidlingsgruppe getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    @Override
    public LocalDateTime getFormidlingsgruppeEndret() {
        return formidlingsgruppeEndret;
    }

    @Override
    public String toString() {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + foedselsnummer != null ? foedselsnummer.maskert() : null +
                ", person_id='" + person_id + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret.toString() +
                "'}'";
    }
}
