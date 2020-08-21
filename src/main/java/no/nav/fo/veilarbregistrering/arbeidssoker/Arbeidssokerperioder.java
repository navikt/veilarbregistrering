package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperioder;

    public static Arbeidssokerperioder of(List<Arbeidssokerperiode> arbeidssokerperioder) {
        return new Arbeidssokerperioder(arbeidssokerperioder);
    }

    public Arbeidssokerperioder(List<Arbeidssokerperiode> arbeidssokerperioder) {
        this.arbeidssokerperioder = arbeidssokerperioder != null ? arbeidssokerperioder : emptyList();
    }

    public Arbeidssokerperioder overlapperMed(Periode forespurtPeriode) {
        return new Arbeidssokerperioder(arbeidssokerperioder.stream()
                .filter(p -> p.getPeriode().overlapperMed(forespurtPeriode))
                .filter(p -> p.getFormidlingsgruppe().erArbeidssoker())
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .collect(toList()));
    }

    public boolean dekkerHele(Periode forespurtPeriode) {
        Optional<Arbeidssokerperiode> eldsteArbeidssokerperiode = arbeidssokerperioder.stream()
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .findFirst();

        return eldsteArbeidssokerperiode
                .map(arbeidssokerperiode -> forespurtPeriode.fraOgMed(arbeidssokerperiode.getPeriode()))
                .orElse(false);
    }

    public List<Arbeidssokerperiode> asList() {
        return arbeidssokerperioder;
    }

    @Override
    public String toString() {
        return "{" +
                "arbeidssokerperioder=" + arbeidssokerperioder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidssokerperioder that = (Arbeidssokerperioder) o;
        return Objects.equals(arbeidssokerperioder, that.arbeidssokerperioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidssokerperioder);
    }
}
