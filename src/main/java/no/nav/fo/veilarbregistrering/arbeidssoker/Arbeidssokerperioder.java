package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode.EldsteFoerst.eldsteFoerst;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperioder;

    public static Arbeidssokerperioder of(List<Arbeidssokerperiode> arbeidssokerperioder) {
        return new Arbeidssokerperioder(arbeidssokerperioder);
    }

    public Arbeidssokerperioder(List<Arbeidssokerperiode> arbeidssokerperioder) {
        this.arbeidssokerperioder = arbeidssokerperioder != null ? arbeidssokerperioder : emptyList();
    }

    public List<Arbeidssokerperiode> overlapperMed(Periode forespurtPeriode) {
        return arbeidssokerperioder.stream()
                .filter(p -> p.getPeriode().overlapperMed(forespurtPeriode))
                .filter(p -> p.getFormidlingsgruppe().erArbeidssoker())
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .collect(toList());
    }

    public boolean dekkerHele(Periode forespurtPeriode) {
        Optional<Arbeidssokerperiode> eldsteArbeidssokerperiode = arbeidssokerperioder.stream()
                .sorted(Comparator.comparing(e -> e.getPeriode().getFra()))
                .findFirst();

        return eldsteArbeidssokerperiode
                .map(arbeidssokerperiode -> forespurtPeriode.fraOgMed(arbeidssokerperiode.getPeriode()))
                .orElse(false);
    }

    public Arbeidssokerperioder slaaSammenMed(List<Arbeidssokerperioder> arbeidssokerperioder) {
        Arbeidssokerperioder arbeidssokerperioderRedusert = arbeidssokerperioder.stream().reduce(
                new Arbeidssokerperioder(null),
                (akkumulertArbeidssokerperioder, gjeldendeArbeidssokerperioder) -> {
                    List<Arbeidssokerperiode> sammensattListe = Stream
                            .concat(
                                    akkumulertArbeidssokerperioder.asList().stream(),
                                    gjeldendeArbeidssokerperioder.asList().stream())
                            .collect(Collectors.toList());
                    return Arbeidssokerperioder.of(sammensattListe);
                });

        List<Arbeidssokerperiode> alleArbeidssokerperioder = Stream
                .concat(this.asList().stream(), arbeidssokerperioderRedusert.asList().stream())
                .sorted(eldsteFoerst())
                .collect(Collectors.toList());

        return Arbeidssokerperioder.of(alleArbeidssokerperioder);
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
}
