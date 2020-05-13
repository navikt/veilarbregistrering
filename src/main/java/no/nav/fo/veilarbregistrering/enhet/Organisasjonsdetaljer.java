package no.nav.fo.veilarbregistrering.enhet;

import java.util.List;
import java.util.Optional;

public class Organisasjonsdetaljer {

    private final List<Forretningsadresse> forretningsadresser;
    private final List<no.nav.fo.veilarbregistrering.enhet.Postadresse> postadresser;

    public static Organisasjonsdetaljer of(
            List<Forretningsadresse> forretningsadresser,
            List<Postadresse> postadresser) {

        return new Organisasjonsdetaljer(forretningsadresser, postadresser);
    }

    public Organisasjonsdetaljer(
            List<Forretningsadresse> forretningsadresser,
            List<Postadresse> postadresser) {

        this.forretningsadresser = forretningsadresser;
        this.postadresser = postadresser;
    }

    public Optional<Kommunenummer> kommunenummer() {
        Optional<Forretningsadresse> muligForretningsadresse = forretningsadresser.stream()
                .filter(Forretningsadresse::erGyldig)
                .findFirst();

        if (muligForretningsadresse.isPresent()) {
            return muligForretningsadresse
                    .map(adresse -> adresse.getKommunenummer());
        }

        Optional<Postadresse> muligPostadresse = postadresser.stream()
                .filter(Postadresse::erGyldig)
                .findFirst();

        if (muligPostadresse.isPresent()) {
            return muligPostadresse
                    .map(adresse -> adresse.getKommunenummer());
        }

        return Optional.empty();
    }

}
