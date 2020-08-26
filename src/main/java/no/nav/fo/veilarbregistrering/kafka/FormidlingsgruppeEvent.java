package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDateTime;
import java.util.Optional;

public class FormidlingsgruppeEvent implements EndretFormidlingsgruppeCommand {

    private final Foedselsnummer foedselsnummer;
    private final String personId;
    private final Operation operation;
    private final Formidlingsgruppe formidlingsgruppe;
    private final LocalDateTime formidlingsgruppeEndret;
    private final Formidlingsgruppe forrigeformidlingsgruppe;
    private final LocalDateTime forrigeformidlingsgruppeEndret;
    private final String personIdStatus;

    public FormidlingsgruppeEvent(
            Foedselsnummer foedselsnummer,
            String personId,
            String personIdStatus,
            Operation operation,
            Formidlingsgruppe formidlingsgruppe,
            LocalDateTime formidlingsgruppeEndret,
            Formidlingsgruppe forrigeformidlingsgruppe,
            LocalDateTime forrigeformidlingsgruppeEndret) {
        this.foedselsnummer = foedselsnummer;
        this.personId = personId;
        this.personIdStatus = personIdStatus;
        this.operation = operation;
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
    public String getPersonIdStatus() {
        return personIdStatus;
    }

    @Override
    public Operation getOperation() {
        return operation;
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
        return Optional.ofNullable(forrigeformidlingsgruppe);
    }

    @Override
    public Optional<LocalDateTime> getForrigeFormidlingsgruppeEndret() {
        return Optional.ofNullable(forrigeformidlingsgruppeEndret);
    }

    @Override
    public String toString() {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + (foedselsnummer != null ? foedselsnummer.maskert() : null) +
                ", personId='" + personId + '\'' +
                ", personIdStatus='" + personIdStatus + '\'' +
                ", operation='" + operation + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret.toString() +
                ", forrigeFormidlingsgruppe=" + (forrigeformidlingsgruppe != null ? forrigeformidlingsgruppe.toString() : null) +
                ", forrigeFormidlingsgruppeEndret=" + (forrigeformidlingsgruppeEndret != null ? forrigeformidlingsgruppeEndret.toString() : null) +
                "'}'";
    }
}
