package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode.EldsteFoerst.eldsteFoerst;
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeRaaData.NyesteFoerst.nyesteFoerst;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperioder;

    public static Arbeidssokerperioder of(List<ArbeidssokerperiodeRaaData> arbeidssokerperiodeRaaData) {
        return new Arbeidssokerperioder(Optional.of(arbeidssokerperiodeRaaData.stream()
                .sorted(nyesteFoerst()).collect(toList()))
                .map(beholdKunSisteEndringPerDagIListen).get().stream()
                .sorted(eldsteFoerst()).collect(toList()));
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

    public Arbeidssokerperioder sorterOgPopulerTilDato() {
        return new Arbeidssokerperioder(
                Optional.of(arbeidssokerperioder.stream()
                        .sorted(eldsteFoerst().reversed())
                        .collect(toList())
                ).map(populerTilDato)
                        .map(a -> a.stream()
                                .sorted(eldsteFoerst())
                                .collect(toList())).get());
    }

    public static Function<List<Arbeidssokerperiode>, List<Arbeidssokerperiode>> populerTilDato =
            (arbeidssokerperioder) -> {
                List<Arbeidssokerperiode> nyListe = new ArrayList(arbeidssokerperioder.size());

                LocalDate nyTildato = null;
                for (Arbeidssokerperiode arbeidssokerperiode : arbeidssokerperioder) {
                    nyListe.add(arbeidssokerperiode.tilOgMed(nyTildato));
                    nyTildato = arbeidssokerperiode.getPeriode().getFra().minusDays(1);
                }
                return nyListe;
            };

    public static Function<List<ArbeidssokerperiodeRaaData>, List<Arbeidssokerperiode>> beholdKunSisteEndringPerDagIListen =
            (arbeidssokerperiodeRaaData) -> {
                List<Arbeidssokerperiode> arbeidssokerperioder = new ArrayList<>(arbeidssokerperiodeRaaData.size());

                LocalDate forrigeEndretDato = null;

                for(ArbeidssokerperiodeRaaData raaDataPeriode : arbeidssokerperiodeRaaData) {
                    LocalDate endretDato = raaDataPeriode.getFormidlingsgruppeEndret().toLocalDateTime().toLocalDate();

                    if(forrigeEndretDato != null && endretDato.isEqual(forrigeEndretDato)) {
                        continue;
                    }

                    arbeidssokerperioder.add(new Arbeidssokerperiode(
                            Formidlingsgruppe.of(raaDataPeriode.getFormidlingsgruppe()),
                            Periode.of(
                                    endretDato,
                                    null
                            )
                    ));

                    forrigeEndretDato = endretDato;
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
