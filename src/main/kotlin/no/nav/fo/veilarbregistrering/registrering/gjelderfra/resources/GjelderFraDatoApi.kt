package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

interface GjelderFraDatoApi {
    fun hentGjelderFraDato(): GjelderFraDatoResponseDto?
    fun lagreGjelderFraDato(datoDto: GjelderFraDatoRequestDto): Any
}
