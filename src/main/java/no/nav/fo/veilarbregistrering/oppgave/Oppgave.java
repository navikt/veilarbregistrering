package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Oppgave {

    private static final Map<OppgaveType, String> beskrivelser = new HashMap<>(2);
    static {
        beskrivelser.put(
                OppgaveType.OPPHOLDSTILLATELSE,
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse."
        );
        beskrivelser.put(
                OppgaveType.UTVANDRET,
                "Brukeren får ikke registrert seg som arbeidssøker fordi bruker står som utvandret i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg vanlig rutine for slike tilfeller."

        );
    }

    private final AktorId aktorId;
    private final Enhetsnr enhetsnr;
    private final OppgaveType oppgaveType;
    private final LocalDate fristFerdigstillelse;
    private final LocalDate aktivDato;

    private Oppgave(AktorId aktorId, Enhetsnr enhetsnr, OppgaveType oppgaveType, LocalDate fristFerdigstillelse, LocalDate aktivDato) {
        this.aktorId = aktorId;
        this.enhetsnr = enhetsnr;
        this.oppgaveType = oppgaveType;
        this.fristFerdigstillelse = fristFerdigstillelse;
        this.aktivDato = aktivDato;
    }

    public static Oppgave opprettOppgave(
            AktorId aktorId,
            Enhetsnr enhetsnr,
            OppgaveType oppgaveType,
            LocalDate dagensDato) {

        return new Oppgave(aktorId, enhetsnr, oppgaveType, fristFerdigstilleles(dagensDato), dagensDato);
    }

    private static LocalDate fristFerdigstilleles(LocalDate dagensDato) {
        return Virkedager.plussAntallArbeidsdager(dagensDato, 2);
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public Optional<Enhetsnr> getEnhetsnr() {
        return Optional.ofNullable(enhetsnr);
    }

    public LocalDate getFristFerdigstillelse() {
        return fristFerdigstillelse;
    }

    public LocalDate getAktivDato() {
        return aktivDato;
    }

    public String getBeskrivelse() {
        return beskrivelser.get(oppgaveType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Oppgave oppgave = (Oppgave) o;
        return Objects.equals(aktorId, oppgave.aktorId) &&
                Objects.equals(enhetsnr, oppgave.enhetsnr) &&
                oppgaveType == oppgave.oppgaveType &&
                Objects.equals(fristFerdigstillelse, oppgave.fristFerdigstillelse) &&
                Objects.equals(aktivDato, oppgave.aktivDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktorId, enhetsnr, oppgaveType, fristFerdigstillelse, aktivDato);
    }
}
