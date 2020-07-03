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
import static no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeDAO.NyesteFoerst.nyesteFoerst;

public class Arbeidssokerperioder {

    private final List<Arbeidssokerperiode> arbeidssokerperioder;

    public static Arbeidssokerperioder of(List<ArbeidssokerperiodeDAO> arbeidssokerperiodeDAOer) {
        List<ArbeidssokerperiodeDAO> perioderSortert = arbeidssokerperiodeDAOer.stream().sorted(nyesteFoerst()).collect(toList());

        List<Arbeidssokerperiode> arbeidssokerperioder = new ArrayList<>(arbeidssokerperiodeDAOer.size());

        LocalDate forrigeEndretDato = null;

        for(ArbeidssokerperiodeDAO arbeidssokerperiodeDAO : perioderSortert) {
            LocalDate endretDato = arbeidssokerperiodeDAO.getFormidlingsgruppeEndret().toLocalDateTime().toLocalDate();

            if(forrigeEndretDato != null && endretDato.isEqual(forrigeEndretDato)) {
                continue;
            }

            arbeidssokerperioder.add(new Arbeidssokerperiode(
                    Formidlingsgruppe.of(arbeidssokerperiodeDAO.getFormidlingsgruppe()),
                    Periode.of(
                            endretDato,
                            null
                    )
            ));

            forrigeEndretDato = endretDato;
        }

        return new Arbeidssokerperioder(arbeidssokerperioder.stream().sorted(eldsteFoerst()).collect(toList()));
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
