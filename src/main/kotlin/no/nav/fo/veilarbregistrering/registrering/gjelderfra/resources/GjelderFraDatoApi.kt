package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

interface GjelderFraDatoApi {
    fun hentGjelderFraDato(): GjelderFraDatoDto?
    fun lagreGjelderFraDato(datoDto: GjelderFraDatoDto): Any
}
