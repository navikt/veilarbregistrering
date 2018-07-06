package no.nav.fo.veilarbregistrering.service;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils;
import no.nav.metrics.MetricsFactory;
import no.nav.metrics.Timer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.util.List;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static java.util.stream.Collectors.toList;
import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ARBEIDSFORHOLD;

@Slf4j
public class ArbeidsforholdService {
    private ArbeidsforholdV3 arbeidsforholdV3;

    public ArbeidsforholdService(ArbeidsforholdV3 arbeidsforholdV3) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
    }

    @Cacheable(HENT_ARBEIDSFORHOLD)
    public List<Arbeidsforhold> hentArbeidsforhold(String fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
        Regelverker regelverker = new Regelverker();
        regelverker.setValue("A_ORDNINGEN");
        request.setRapportertSomRegelverk(regelverker);
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fnr);
        request.setIdent(ident);

        FinnArbeidsforholdPrArbeidstakerResponse response;

        Timer timer = MetricsFactory.createTimer("finn.arbeidsforhold.aareg").start();
        response = Try.of(() -> arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request))
                .onFailure(f -> {
                    timer.stop()
                            .setFailed()
                            .addTagToReport("aarsak",  f.getClass().getSimpleName())
                            .report();
                    log.error("Feil ved henting av arbeidsforhold for bruker", f);
                })
                .mapFailure(
                        Case($(instanceOf(FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning.class)), (t) -> new ForbiddenException("Ikke tilgang til bruker")),
                        Case($(instanceOf(FinnArbeidsforholdPrArbeidstakerUgyldigInput.class)), (t) -> new BadRequestException("Ugyldig bruker identifikator"))
                )
                .onSuccess((event) -> timer.stop().report())
                .get();

        return response.getArbeidsforhold().stream()
                .map(Arbeidsforhold::of)
                .collect(toList());
    }

    public Arbeidsforhold hentSisteArbeidsforhold(String fnr) {
        return ArbeidsforholdUtils.hentSisteArbeidsforhold(hentArbeidsforhold(fnr));
    }

}
