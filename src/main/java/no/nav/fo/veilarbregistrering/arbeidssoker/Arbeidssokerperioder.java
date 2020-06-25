package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperiodes;

    public Arbeidssokerperioder(List<Arbeidssokerperiode> arbeidssokerperiodes) {
        this.arbeidssokerperiodes = arbeidssokerperiodes != null ? arbeidssokerperiodes : emptyList();
    }

    public List<Arbeidssokerperiode> overlapperMed(Periode forespurtPeriode) {
        return arbeidssokerperiodes.stream()
                .filter(p -> p.getPeriode().overlapperMed(forespurtPeriode))
                .filter(p -> p.getFormidlingsgruppe().erArbeidssoker())
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .collect(Collectors.toList());
    }

    public boolean dekkerHele(Periode forespurtPeriode) {
        Optional<Arbeidssokerperiode> eldsteArbeidssokerperiode = arbeidssokerperiodes.stream()
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .findFirst();

        return eldsteArbeidssokerperiode
                .map(arbeidssokerperiode -> forespurtPeriode.fraOgMed(arbeidssokerperiode.getPeriode()))
                .orElse(false);
    }

    public List<Arbeidssokerperiode> asList() {
        return arbeidssokerperiodes;
    }
}
