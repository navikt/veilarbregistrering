package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;

class KontaktinfoMapper {

    private KontaktinfoMapper() {
    }

    static KontaktinfoDto map(Kontaktinfo kontaktinfo) {
        KontaktinfoDto kontaktinfoDto = new KontaktinfoDto();
        kontaktinfoDto.setEpost(kontaktinfo.getEpost());
        kontaktinfoDto.setTelefon(kontaktinfo.getTelefon());

        return kontaktinfoDto;
    }
}