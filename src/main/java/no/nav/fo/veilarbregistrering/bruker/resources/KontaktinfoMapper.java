package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer;

class KontaktinfoMapper {

    private KontaktinfoMapper() {
    }

    static KontaktinfoDto map(Kontaktinfo kontaktinfo) {
        KontaktinfoDto kontaktinfoDto = new KontaktinfoDto();
        kontaktinfoDto.setTelefonnummerHosKrr(kontaktinfo.getTelefonnummerFraKrr()
                .orElse(null));
        kontaktinfoDto.setTelefonnummerHosNav(kontaktinfo.getTelefonnummerFraNav()
                .map(Telefonnummer::asLandkodeOgNummer)
                .orElse(null));

        return kontaktinfoDto;
    }
}