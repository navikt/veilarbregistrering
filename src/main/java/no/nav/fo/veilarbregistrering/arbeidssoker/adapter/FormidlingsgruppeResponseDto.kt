package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

data class FormidlingsgruppeResponseDto(
    val personId: String,
    val fodselsnr: String,
    val formidlingshistorikk: List<FormidlingshistorikkDto>?
)