package no.nav.fo.veilarbregistrering.oppfolging;

public interface Oppfolgingsstatus {

    Boolean getKanReaktiveres();

    Boolean getErSykmeldtMedArbeidsgiver();

    boolean isUnderOppfolging();

    String getFormidlingsgruppe();

    String getServicegruppe();
}
