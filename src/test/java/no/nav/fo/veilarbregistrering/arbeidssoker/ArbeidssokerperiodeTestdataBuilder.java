package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.time.LocalDate;

public class ArbeidssokerperiodeTestdataBuilder implements Builder<Arbeidssokerperiode> {

    private LocalDate fra;
    private LocalDate til;
    private Formidlingsgruppe formidlingsgruppe;

    public static ArbeidssokerperiodeTestdataBuilder medArbs() {
        return new ArbeidssokerperiodeTestdataBuilder(Formidlingsgruppe.of("ARBS"));
    }

    public static ArbeidssokerperiodeTestdataBuilder medIserv() {
        return new ArbeidssokerperiodeTestdataBuilder(Formidlingsgruppe.of("ISERV"));
    }

    private ArbeidssokerperiodeTestdataBuilder(Formidlingsgruppe formidlingsgruppe) {
        this.formidlingsgruppe = formidlingsgruppe;
    }

    @Override
    public Arbeidssokerperiode build() {
        return new Arbeidssokerperiode(formidlingsgruppe, Periode.of(fra, til));
    }

    public ArbeidssokerperiodeTestdataBuilder fra(LocalDate fra) {
        this.fra = fra;
        return this;
    }

    public ArbeidssokerperiodeTestdataBuilder til(LocalDate til) {
        this.til = til;
        return this;
    }
}
