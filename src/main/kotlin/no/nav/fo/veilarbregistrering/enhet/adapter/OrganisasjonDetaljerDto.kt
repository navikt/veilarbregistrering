package no.nav.fo.veilarbregistrering.enhet.adapter

class OrganisasjonDetaljerDto(
    val forretningsadresser: List<ForretningsAdresseDto> = emptyList(),
    val postadresser: List<PostadresseDto> = emptyList()
)