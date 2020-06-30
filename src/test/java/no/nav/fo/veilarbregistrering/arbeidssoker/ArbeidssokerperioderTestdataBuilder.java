package no.nav.fo.veilarbregistrering.arbeidssoker;

import java.util.ArrayList;
import java.util.List;

public class ArbeidssokerperioderTestdataBuilder {

    private List<Arbeidssokerperiode> arbeidssokerperioder;

    public static ArbeidssokerperioderTestdataBuilder arbeidssokerperioder() {
        return new ArbeidssokerperioderTestdataBuilder();
    }

    private ArbeidssokerperioderTestdataBuilder() {
        this.arbeidssokerperioder = new ArrayList<>();
    }

    public ArbeidssokerperioderTestdataBuilder periode(ArbeidssokerperiodeTestdataBuilder arbeidssokerperiode) {
        this.arbeidssokerperioder.add(arbeidssokerperiode.build());
        return this;
    }

    public Arbeidssokerperioder build() {
        return new Arbeidssokerperioder(arbeidssokerperioder);
    }
}
