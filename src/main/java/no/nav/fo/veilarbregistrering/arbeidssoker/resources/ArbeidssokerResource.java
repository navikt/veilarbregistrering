package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import no.nav.common.abac.Pep;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/arbeidssoker")
public class ArbeidssokerResource implements ArbeidssokerApi {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerResource.class);

    private final ArbeidssokerService arbeidssokerService;
    private final UserService userService;
    private final Pep pepClient;

    public ArbeidssokerResource(
            ArbeidssokerService arbeidssokerService,
            UserService userService,
            Pep pepClient) {
        this.arbeidssokerService = arbeidssokerService;
        this.userService = userService;
        this.pepClient = pepClient;
    }

    @Override
    @GetMapping("/perioder")
    public ArbeidssokerperioderDto hentArbeidssokerperioder(
            @RequestParam("fnr") String fnr,
            @RequestParam("fraOgMed") LocalDate fraOgMed,
            @RequestParam("tilOgMed") LocalDate tilOgMed
    ) {
        Bruker bruker = userService.finnBrukerGjennomPdl();

        // TODO pepClient.sjekkLesetilgangTilBruker(BrukerAdapter.map(bruker));

        Arbeidssokerperioder arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(
                bruker, Periode.gyldigPeriode(fraOgMed, tilOgMed));

        LOG.info(String.format("Ferdig med henting av arbeidssokerperioder - fant %s perioder", arbeidssokerperiodes.asList().size()));

        return map(arbeidssokerperiodes.eldsteFoerst());
    }

    private ArbeidssokerperioderDto map(List<Arbeidssokerperiode> arbeidssokerperioder) {
        List<ArbeidssokerperiodeDto> arbeidssokerperiodeDtoer = arbeidssokerperioder.stream()
                .map(periode -> new ArbeidssokerperiodeDto(
                        periode.getPeriode().getFra().toString(),
                        ofNullable(periode.getPeriode().getTil())
                                .map(LocalDate::toString)
                                .orElse(null)))
                .collect(Collectors.toList());

        return new ArbeidssokerperioderDto(arbeidssokerperiodeDtoer);
    }
}