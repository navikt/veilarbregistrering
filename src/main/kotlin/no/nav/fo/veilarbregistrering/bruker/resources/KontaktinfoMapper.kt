package no.nav.fo.veilarbregistrering.bruker.resources

import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo

internal object KontaktinfoMapper {
    fun map(kontaktinfo: Kontaktinfo): KontaktinfoDto {
        return KontaktinfoDto(
            kontaktinfo.telefonnummerFraKrr,
            kontaktinfo.telefonnummerFraNav?.toString(),
            kontaktinfo.navn
        )
    }
}