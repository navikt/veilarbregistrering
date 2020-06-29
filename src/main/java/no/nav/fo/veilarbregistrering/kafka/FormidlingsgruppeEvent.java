package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDateTime;
import java.util.Optional;

public class FormidlingsgruppeEvent implements EndretFormidlingsgruppeCommand {

    private final Foedselsnummer foedselsnummer;
    private final String personId;
    private final Formidlingsgruppe formidlingsgruppe;
    private final LocalDateTime formidlingsgruppeEndret;
    private final Formidlingsgruppe forrigeformidlingsgruppe;
    private final LocalDateTime forrigeformidlingsgruppeEndret;

    public FormidlingsgruppeEvent(
            Foedselsnummer foedselsnummer,
            String personId,
            Formidlingsgruppe formidlingsgruppe,
            LocalDateTime formidlingsgruppeEndret,
            Formidlingsgruppe forrigeformidlingsgruppe,
            LocalDateTime forrigeformidlingsgruppeEndret) {
        this.foedselsnummer = foedselsnummer;
        this.personId = personId;
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
        this.forrigeformidlingsgruppe = forrigeformidlingsgruppe;
        this.forrigeformidlingsgruppeEndret = forrigeformidlingsgruppeEndret;
    }

    @Override
    public Optional<Foedselsnummer> getFoedselsnummer() {
        return Optional.ofNullable(foedselsnummer);
    }

    @Override
    public String getPersonId() {
        return personId;
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
    public Optional<Formidlingsgruppe> getForrigeFormidlingsgruppe() {
        return Optional.ofNullable(formidlingsgruppe);
    }

    @Override
    public Optional<LocalDateTime> getForrigeFormidlingsgruppeEndret() {
        return Optional.ofNullable(forrigeformidlingsgruppeEndret);
    }

    @Override
    public String toString() {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + foedselsnummer != null ? foedselsnummer.maskert() : null +
                ", person_id='" + personId + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret.toString() +
                ", forrigeFormidlingsgruppe=" + forrigeformidlingsgruppe != null ? forrigeformidlingsgruppe.toString() : null +
                ", forrigeFormidlingsgruppeEndret=" + forrigeformidlingsgruppeEndret != null ? forrigeformidlingsgruppeEndret.toString() : null +
                "'}'";
    }
}
