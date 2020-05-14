package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        boolean tekniskFeil = false;

        Optional<Person> person;
        try {
            person = pdlOppslagGateway.hentPerson(bruker.getAktorId());
        } catch (RuntimeException e) {
            LOG.error("Hent kontaktinfo ", e);
            person = Optional.empty();
            tekniskFeil = true;
        }

        Optional<Telefonnummer> telefonnummer;
        try {
            telefonnummer = krrGateway.hentKontaktinfo(bruker);
        } catch (RuntimeException e) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet", e);
            telefonnummer = Optional.empty();
            tekniskFeil = true;
        }

        if (person.isPresent() || telefonnummer.isPresent()) {
            return Kontaktinfo.of(
                    person.map(p -> p.getTelefonnummer()
                            .map(t -> t.asLandkodeOgNummer()).orElse(null))
                            .orElse(null),
                    telefonnummer
                            .orElse(null)
            );
        }

        if (tekniskFeil) {
            throw new Feil(FeilType.UKJENT);
        } else {
            throw new Feil(FeilType.FINNES_IKKE);
        }
    }
}
