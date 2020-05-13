package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.enhet.Postadresse;

import java.util.List;

import static java.util.stream.Collectors.toList;

class OrganisasjonsdetaljerMapper {

    private OrganisasjonsdetaljerMapper() {
    }

    static Organisasjonsdetaljer map(OrganisasjonDetaljerDto organisasjonDetaljerDto) {
        return Organisasjonsdetaljer.of(
                mapForretningsadresse(organisasjonDetaljerDto.getForretningsadresser()),
                mapPostadresse(organisasjonDetaljerDto.getPostadresser()));
    }

    private static List<Forretningsadresse> mapForretningsadresse(List<ForretningsAdresseDto> forretningsadresser) {
        return forretningsadresser.stream()
                .map(OrganisasjonsdetaljerMapper::map)
                .collect(toList());
    }

    private static Forretningsadresse map(ForretningsAdresseDto adresse) {
        return new Forretningsadresse(
                Kommunenummer.of(adresse.getKommunenummer()),
                map(adresse.getGyldighetsperiode()));
    }

    private static List<Postadresse> mapPostadresse(List<PostadresseDto> postadresser) {
        return postadresser.stream()
                .map(OrganisasjonsdetaljerMapper::map)
                .collect(toList());
    }

    private static Postadresse map(PostadresseDto adresse) {
        return new Postadresse(
                Kommunenummer.of(adresse.getKommunenummer()),
                map(adresse.getGyldighetsperiode()));
    }

    private static Periode map(GyldighetsperiodeDto gyldighetsperiodeDto) {
        return Periode.of(
                gyldighetsperiodeDto.getFom(),
                gyldighetsperiodeDto.getTom());
    }

}
