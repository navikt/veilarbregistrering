package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EndretFormidlingsgruppeCommand {

    Optional<Foedselsnummer> getFoedselsnummer();

    String getPersonId();

    Operation getOperation();

    Formidlingsgruppe getFormidlingsgruppe();

    LocalDateTime getFormidlingsgruppeEndret();

    Optional<Formidlingsgruppe> getForrigeFormidlingsgruppe();

    Optional<LocalDateTime> getForrigeFormidlingsgruppeEndret();
}
