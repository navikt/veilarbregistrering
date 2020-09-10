package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KontaktinfoService {

    private static final Logger LOG = LoggerFactory.getLogger(KontaktinfoService.class);

    private final PdlOppslagGateway pdlOppslagGateway;
    private final KrrGateway krrGateway;

    public KontaktinfoService(PdlOppslagGateway pdlOppslagGateway, KrrGateway krrGateway) {
        this.pdlOppslagGateway = pdlOppslagGateway;
        this.krrGateway = krrGateway;
    }

    public Kontaktinfo hentKontaktinfo(Bruker bruker) {

        List<FeilType> feiltyper = new ArrayList<>(2);

        Optional<Person> person;
        try {
            person = pdlOppslagGateway.hentPerson(bruker.getAktorId());
            person.ifPresent(p -> LOG.info(String.format("Fant person fra PDL: %s", p)));
        } catch (RuntimeException e) {
            LOG.error("Hent kontaktinfo fra PDL feilet", e);
            person = Optional.empty();
            feiltyper.add(FeilType.UKJENT);
        }

        Optional<Telefonnummer> telefonnummer;
        try {
            telefonnummer = krrGateway.hentKontaktinfo(bruker);

        } catch (NotAuthorizedException | ForbiddenException e) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet pga manglende tilgang", e);
            telefonnummer = Optional.empty();
            feiltyper.add(FeilType.INGEN_TILGANG);

        } catch (RuntimeException e) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet av ukjent grunn", e);
            telefonnummer = Optional.empty();
            feiltyper.add(FeilType.UKJENT);
        }

        if (fantMinstEttTelefonnummer(person, telefonnummer)) {
            return opprettKontaktinfo(person, telefonnummer);
        }

        if (feiltyper.contains(FeilType.INGEN_TILGANG)) {
            throw new Feil(FeilType.INGEN_TILGANG);
        }
        if (feiltyper.contains(FeilType.UKJENT)) {
            throw new Feil(FeilType.UKJENT);
        }
        throw new Feil(FeilType.FINNES_IKKE);
    }

    private boolean fantMinstEttTelefonnummer(Optional<Person> person, Optional<Telefonnummer> telefonnummer) {
        return (person.isPresent() && person.get().getTelefonnummer().isPresent())
                || telefonnummer.isPresent();
    }

    private Kontaktinfo opprettKontaktinfo(Optional<Person> person, Optional<Telefonnummer> telefonnummer) {
        return Kontaktinfo.of(
                person.map(p -> p.getTelefonnummer()
                        .map(t -> t.asLandkodeOgNummer()).orElse(null))
                        .orElse(null),
                telefonnummer
                        .orElse(null)
        );
    }
}
