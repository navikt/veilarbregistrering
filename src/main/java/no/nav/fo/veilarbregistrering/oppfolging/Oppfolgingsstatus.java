package no.nav.fo.veilarbregistrering.oppfolging;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Oppfolgingsstatus {

    private boolean underOppfolging;
    private Boolean kanReaktiveres;
    private Boolean erSykmeldtMedArbeidsgiver;
    private Formidlingsgruppe formidlingsgruppe;
    private Servicegruppe servicegruppe;
    private Rettighetsgruppe rettighetsgruppe;

    public Oppfolgingsstatus(
            boolean underOppfolging,
            Boolean kanReaktiveres,
            Boolean erSykmeldtMedArbeidsgiver,
            Formidlingsgruppe formidlingsgruppe,
            Servicegruppe servicegruppe,
            Rettighetsgruppe rettighetsgruppe) {
        this.underOppfolging = underOppfolging;
        this.kanReaktiveres = kanReaktiveres;
        this.erSykmeldtMedArbeidsgiver = erSykmeldtMedArbeidsgiver;
        this.formidlingsgruppe = formidlingsgruppe;
        this.servicegruppe = servicegruppe;
        this.rettighetsgruppe = rettighetsgruppe;
    }

    public Optional<Boolean> getKanReaktiveres() {
        return ofNullable(kanReaktiveres);
    }

    public Optional<Boolean> getErSykmeldtMedArbeidsgiver() {
        return ofNullable(erSykmeldtMedArbeidsgiver);
    }

    public boolean isUnderOppfolging() {
        return underOppfolging;
    }

    public Optional<Formidlingsgruppe> getFormidlingsgruppe() {
        return ofNullable(formidlingsgruppe);
    }

    public Optional<Servicegruppe> getServicegruppe() {
        return ofNullable(servicegruppe);
    }

    public Optional<Rettighetsgruppe> getRettighetsgruppe() {
        return ofNullable(rettighetsgruppe);
    }
}
