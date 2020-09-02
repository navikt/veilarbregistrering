package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode.EldsteFoerst.eldsteFoerst;
import static no.nav.fo.veilarbregistrering.db.arbeidssoker.Formidlingsgruppeendring.NyesteFoerst.nyesteFoerst;

class ArbeidssokerperioderMapper {

    static Arbeidssokerperioder map(List<Formidlingsgruppeendring> formidlingsgruppeendringer) {
        return new Arbeidssokerperioder(
                Optional.of(
                        formidlingsgruppeendringer.stream()
                                .sorted(nyesteFoerst())
                                .collect(toList()))
                        .map(beholdKunEndringerForAktiveIdenter)
                        .map(beholdKunSisteEndringPerDagIListen)
                        .map(populerTilDato)
                        .get()
                        .stream()
                        .sorted(eldsteFoerst())
                        .collect(toList()));
    }

    private static Function<List<Formidlingsgruppeendring>, List<Formidlingsgruppeendring>> beholdKunEndringerForAktiveIdenter =
            (formidlingsgruppeendringer) -> formidlingsgruppeendringer.stream()
                    .filter(Formidlingsgruppeendring::erAktiv)
                    .collect(toList());

    private static Function<List<Formidlingsgruppeendring>, List<Arbeidssokerperiode>> beholdKunSisteEndringPerDagIListen =
            (formidlingsgruppeendringer) -> {
                List<Arbeidssokerperiode> arbeidssokerperioder = new ArrayList<>(formidlingsgruppeendringer.size());

                LocalDate forrigeEndretDato = null;

                for (Formidlingsgruppeendring formidlingsgruppeendring : formidlingsgruppeendringer) {
                    LocalDate endretDato = formidlingsgruppeendring.getFormidlingsgruppeEndret().toLocalDateTime().toLocalDate();

                    if (forrigeEndretDato != null && endretDato.isEqual(forrigeEndretDato)) {
                        continue;
                    }

                    arbeidssokerperioder.add(new Arbeidssokerperiode(
                            Formidlingsgruppe.of(formidlingsgruppeendring.getFormidlingsgruppe()),
                            Periode.of(
                                    endretDato,
                                    null
                            )
                    ));

                    forrigeEndretDato = endretDato;
                }

                return arbeidssokerperioder;
            };

    private static Function<List<Arbeidssokerperiode>, List<Arbeidssokerperiode>> populerTilDato =
            (arbeidssokerperioder) -> {
                List<Arbeidssokerperiode> nyListe = new ArrayList(arbeidssokerperioder.size());

                LocalDate nyTildato = null;
                for (Arbeidssokerperiode arbeidssokerperiode : arbeidssokerperioder) {
                    nyListe.add(arbeidssokerperiode.tilOgMed(nyTildato));
                    nyTildato = arbeidssokerperiode.getPeriode().getFra().minusDays(1);
                }
                return nyListe;
            };
}
