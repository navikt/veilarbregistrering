package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.*;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperioder;

    public Arbeidssokerperioder(List<Arbeidssokerperiode> arbeidssokerperioder) {
        this.arbeidssokerperioder = arbeidssokerperioder != null ? arbeidssokerperioder : emptyList();
    }

    public List<Arbeidssokerperiode> overlapperMed(Periode forespurtPeriode) {
        return arbeidssokerperioder.stream()
                .filter(p -> p.getPeriode().overlapperMed(forespurtPeriode))
                .filter(p -> p.getFormidlingsgruppe().erArbeidssoker())
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .collect(Collectors.toList());
    }

    public boolean dekkerHele(Periode forespurtPeriode) {
        Optional<Arbeidssokerperiode> eldsteArbeidssokerperiode = arbeidssokerperioder.stream()
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .findFirst();

        return eldsteArbeidssokerperiode
                .map(arbeidssokerperiode -> forespurtPeriode.fraOgMed(arbeidssokerperiode.getPeriode()))
                .orElse(false);
    }

    public Arbeidssokerperioder sorterOgPopulerTilDato() {
        return new Arbeidssokerperioder(of(this.arbeidssokerperioder.stream()
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra())).collect(Collectors.toList()))
                .map(populerTilDato).get());
    }

    private static Function<List<Arbeidssokerperiode>, List<Arbeidssokerperiode>> populerTilDato =
        (arbeidssokerperioder) -> {
            for (int i = 0; i < arbeidssokerperioder.size() - 1; i++) {
                arbeidssokerperioder.set(i, arbeidssokerperioder.get(i).kopiMedNyTilDato(arbeidssokerperioder.get(i + 1).getPeriode().getFra().minusDays(1)));
            }
            return arbeidssokerperioder;
        };

    public List<Arbeidssokerperiode> asList() {
        return arbeidssokerperioder;
    }

    @Override
    public String toString() {
        return "{" +
                "arbeidssokerperioder=" + arbeidssokerperioder +
                '}';
    }
}
