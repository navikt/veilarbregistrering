package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.enhet.Forretningsadresse
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer
import no.nav.fo.veilarbregistrering.enhet.Postadresse

internal object OrganisasjonsdetaljerMapper {
    fun map(organisasjonDetaljerDto: OrganisasjonDetaljerDto): Organisasjonsdetaljer =
        Organisasjonsdetaljer.of(
            mapForretningsadresse(organisasjonDetaljerDto.forretningsadresser),
            mapPostadresse(organisasjonDetaljerDto.postadresser)
        )

    private fun mapForretningsadresse(forretningsadresser: List<ForretningsAdresseDto>): List<Forretningsadresse> =
        forretningsadresser
            .map(::map)

    private fun mapPostadresse(postadresser: List<PostadresseDto>): List<Postadresse> =
        postadresser
            .map(::map)

    private fun map(adresse: ForretningsAdresseDto): Forretningsadresse =
        Forretningsadresse(
            Kommunenummer.of(adresse.kommunenummer),
            map(adresse.gyldighetsperiode)
        )

    private fun map(adresse: PostadresseDto): Postadresse =
        Postadresse(
            Kommunenummer.of(adresse.kommunenummer),
            map(adresse.gyldighetsperiode)
        )

    private fun map(gyldighetsperiodeDto: GyldighetsperiodeDto): Periode =
        Periode.of(
            gyldighetsperiodeDto.fom,
            gyldighetsperiodeDto.tom
        )
}