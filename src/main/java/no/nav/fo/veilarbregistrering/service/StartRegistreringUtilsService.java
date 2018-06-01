package no.nav.fo.veilarbregistrering.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils.oppfyllerKravOmArbeidserfaring;
import static no.nav.fo.veilarbregistrering.utils.DateUtils.erDatoEldreEnnEllerLikAar;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

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

        return oppfyllerKravOmInaktivitet(dagensDato, inaktiveringsdato) &&
                oppfyllerKravOmAlder(alder) &&
                oppfyllerKravOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    private boolean oppfyllerKravOmAlder(int alder) {
        try {
            Integer minAlder = Integer.parseInt(getRequiredProperty(MIN_ALDER_AUTOMATISK_REGISTRERING));
            Integer maksAlder = Integer.parseInt(getRequiredProperty(MAX_ALDER_AUTOMATISK_REGISTRERING));
            return alder >= minAlder && alder <= maksAlder;
        } catch (NumberFormatException e){
            log.error("Feil ved lesing av parametrene " + MAX_ALDER_AUTOMATISK_REGISTRERING + " eller " +
                    MIN_ALDER_AUTOMATISK_REGISTRERING, e);

            return false;
        }
    }

    private boolean oppfyllerKravOmInaktivitet(LocalDate dagensDato, LocalDate inaktiveringsdato) {
        return Objects.isNull(inaktiveringsdato) || erDatoEldreEnnEllerLikAar(dagensDato, inaktiveringsdato, ANTALL_AAR_ISERV);
    }
}
