package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import io.vavr.control.Try;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.metrics.MetricsFactory;
import no.nav.metrics.Timer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static java.util.stream.Collectors.toList;
import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ARBEIDSFORHOLD;

public class ArbeidsforholdGatewayImpl implements ArbeidsforholdGateway {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdGatewayImpl.class);

    private final ArbeidsforholdV3 arbeidsforholdV3;

    public ArbeidsforholdGatewayImpl(ArbeidsforholdV3 arbeidsforholdV3) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
    }

    @Override
    @Cacheable(HENT_ARBEIDSFORHOLD)
    public FlereArbeidsforhold hentArbeidsforhold(String fnr) {
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
                            .addTagToReport("aarsak", f.getClass().getSimpleName())
                            .report();
                    LOG.warn("Feil ved henting av arbeidsforhold for bruker", f);
                })
                .mapFailure(
                        Case($(instanceOf(FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning.class)),
                                (t) -> new ForbiddenException("Henting av arbeidsforhold feilet pga. manglende tilgang til bruker")),
                        Case($(instanceOf(FinnArbeidsforholdPrArbeidstakerUgyldigInput.class)),
                                (t) -> new BadRequestException("Henting av arbeidsforhold feilet pga. ugyldig brukeridentifikator"))
                )
                .onSuccess((event) -> timer.stop().report())
                .get();

        return FlereArbeidsforhold.of(
                response.getArbeidsforhold().stream()
                        .map(ArbeidsforholdMapper::map)
                        .collect(toList()));
    }

}
