package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;

class KontaktinfoMapper {

    private KontaktinfoMapper() {
    }

    static KontaktinfoDto map(Kontaktinfo kontaktinfo) {
        KontaktinfoDto kontaktinfoDto = new KontaktinfoDto();
        kontaktinfoDto.setTelefonnummerHosKrr(kontaktinfo.getTelefonnummerFraKrr()
                .orElse(null));
        kontaktinfoDto.setTelefonnummerHosNav(kontaktinfo.getTelefonnummerFraNav()
                .map(telefonnummer -> telefonnummer.asLandkodeOgNummer())
                .orElse(null));

        return kontaktinfoDto;
    }
}