package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EndretFormidlingsgruppeCommand {

    Optional<Foedselsnummer> getFoedselsnummer();

    String getPersonId();

    String getPersonIdStatus();

    Operation getOperation();

    Formidlingsgruppe getFormidlingsgruppe();

    LocalDateTime getFormidlingsgruppeEndret();

    Optional<Formidlingsgruppe> getForrigeFormidlingsgruppe();

    Optional<LocalDateTime> getForrigeFormidlingsgruppeEndret();

}
