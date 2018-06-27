package no.nav.fo.veilarbregistrering.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static no.nav.fo.veilarbregistrering.utils.DateUtils.erDatoEldreEnnEllerLikAar;

@Slf4j
public class StartRegistreringUtilsService {

    private static final int ANTALL_AAR_ISERV = 2;
    public static final String MIN_ALDER_AUTOMATISK_REGISTRERING = "MIN_ALDER_AUTOMATISK_REGISTRERING";
    public static final String MAX_ALDER_AUTOMATISK_REGISTRERING = "MAKS_ALDER_AUTOMATISK_REGISTRERING";

    public boolean oppfyllerKravOmAutomatiskRegistrering(String fnr, Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
                                                         AktivStatus aktivStatus, LocalDate dagensDato) {
        LocalDate fodselsdato = FnrUtils.utledFodselsdatoForFnr(fnr);
        int alder = FnrUtils.antallAarSidenDato(fodselsdato, dagensDato);
        LocalDate inaktiveringsdato = Optional.of(aktivStatus).map(AktivStatus::getInaktiveringDato).orElse(null);

        return oppfyllerBetingelseOmInaktivitet(dagensDato, inaktiveringsdato) &&
                ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    public boolean oppfyllerBetingelseOmArbeidserfaring(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    public boolean oppfyllerBetingelseOmInaktivitet(LocalDate dagensDato, LocalDate inaktiveringsdato) {
        return Objects.isNull(inaktiveringsdato) || erDatoEldreEnnEllerLikAar(dagensDato, inaktiveringsdato, ANTALL_AAR_ISERV);
    }
}
