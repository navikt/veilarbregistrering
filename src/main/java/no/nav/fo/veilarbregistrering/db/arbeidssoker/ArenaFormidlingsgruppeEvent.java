package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.sql.Timestamp;

public class ArenaFormidlingsgruppeEvent {
    private final Foedselsnummer foedselsnummer;
    private final Formidlingsgruppe formidlingsgruppe;
    private final Timestamp formidlingsgruppeEndret;

    public ArenaFormidlingsgruppeEvent(Foedselsnummer foedselsnummer, Formidlingsgruppe formidlingsgruppe, Timestamp formidlingsgruppeEndret) {
        this.foedselsnummer = foedselsnummer;
        this.formidlingsgruppe = formidlingsgruppe;
        this.formidlingsgruppeEndret = formidlingsgruppeEndret;
    }

    public static ArenaFormidlingsgruppeEvent of(
            Foedselsnummer foedselsnummer,
            Formidlingsgruppe formidlingsgruppe,
            Timestamp formidlingsgruppeEndret) {
        return new ArenaFormidlingsgruppeEvent(foedselsnummer, formidlingsgruppe, formidlingsgruppeEndret);
    }

    public Foedselsnummer getFoedselsnummer() {
        return foedselsnummer;
    }

    public Formidlingsgruppe getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    public Timestamp getFormidlingsgruppeEndret() {
        return formidlingsgruppeEndret;
    }
}
