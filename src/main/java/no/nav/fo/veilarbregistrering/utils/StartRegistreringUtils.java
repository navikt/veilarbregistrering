package no.nav.fo.veilarbregistrering.utils;


import io.vavr.control.Try;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Integer.getInteger;
import static java.util.Objects.nonNull;
import static no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils.oppfyllerKravOmArbeidserfaring;
import static no.nav.fo.veilarbregistrering.utils.ArenaUtils.erUnderOppfolging;
import static no.nav.fo.veilarbregistrering.utils.DateUtils.erDatoEldreEnnEllerLikAar;


public class StartRegistreringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(StartRegistreringUtils.class);

    static final int ANTALL_AAR_ISERV = 2;
    public static final String MIN_ALDER_AUTOMATISK_REGISTRERING = "min.alder.automatisk.registrering";
    public static final String MAX_ALDER_AUTOMATISK_REGISTRERING = "maks.alder.automatisk.registrering";

    public static boolean oppfyllerKravOmAutomatiskRegistrering(String fnr, Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
                                                                OppfolgingStatus oppfolgingStatus, LocalDate dagensDato) {
        LocalDate fodselsdato = FnrUtils.utledFodselsdatoForFnr(fnr);
        int alder = FnrUtils.antallAarSidenDato(fodselsdato, dagensDato);
        LocalDate inaktiveringsdato = Optional.ofNullable(oppfolgingStatus).map(OppfolgingStatus::getInaktiveringsdato).orElse(null);

        return oppfyllerKravOmInaktivitet(dagensDato, inaktiveringsdato) &&
                oppfyllerKravOmAlder(alder) &&
                oppfyllerKravOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    public static boolean erUnderoppfolgingIArena(OppfolgingStatus arenaOppfolging) {
        return erUnderOppfolging(
                arenaOppfolging.getFormidlingsgruppe(),
                arenaOppfolging.getServicegruppe(),
                arenaOppfolging.getHarMottaOppgaveIArena());
    }

    static boolean oppfyllerKravOmAlder(int alder) {
        Integer minAlderAutomatiskRegistrering = getInteger(MIN_ALDER_AUTOMATISK_REGISTRERING);
        Integer maksAlderAutomatiskRegistrering = getInteger(MAX_ALDER_AUTOMATISK_REGISTRERING);

        if (nonNull(minAlderAutomatiskRegistrering) && nonNull(maksAlderAutomatiskRegistrering)) {
            return alder >= minAlderAutomatiskRegistrering && alder <= maksAlderAutomatiskRegistrering;
        } else {
            LOG.error("Parametrene " + MAX_ALDER_AUTOMATISK_REGISTRERING + " eller " +
                    MIN_ALDER_AUTOMATISK_REGISTRERING + " mangler eller har ugyldig innhold.");
            return false;
        }
    }

    static boolean oppfyllerKravOmInaktivitet(LocalDate dagensDato, LocalDate inaktiveringsdato) {
        return Objects.isNull(inaktiveringsdato) || erDatoEldreEnnEllerLikAar(dagensDato, inaktiveringsdato, ANTALL_AAR_ISERV);
    }

    public static <T extends Throwable> void sjekkLesetilgangOrElseThrow(String fnr, PepClient pepClient, Function<Throwable, T> exceptionMapper) throws T {
        Try.of(() -> pepClient.sjekkLeseTilgangTilFnr(fnr))
                .getOrElseThrow(exceptionMapper);
    }
}
