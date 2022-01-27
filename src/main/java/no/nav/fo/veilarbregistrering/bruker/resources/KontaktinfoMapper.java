package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;

class KontaktinfoMapper {

    private KontaktinfoMapper() {
    }

    static KontaktinfoDto map(Kontaktinfo kontaktinfo) {
        KontaktinfoDto kontaktinfoDto = new KontaktinfoDto(
                kontaktinfo.getTelefonnummerFraKrr().orElse(null),
                kontaktinfo.getTelefonnummerFraNav().map(Object::toString).orElse(null),
                kontaktinfo.getNavn()
        );
        return kontaktinfoDto;
    }
}