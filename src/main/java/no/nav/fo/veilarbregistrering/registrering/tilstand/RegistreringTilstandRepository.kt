package no.nav.fo.veilarbregistrering.registrering.tilstand

interface RegistreringTilstandRepository {
    fun lagre(registreringTilstand: RegistreringTilstand): Long
    fun oppdater(registreringTilstand: RegistreringTilstand): RegistreringTilstand
    fun hentRegistreringTilstand(id: Long): RegistreringTilstand
    fun finnRegistreringTilstanderMed(status: Status): List<RegistreringTilstand>
    fun finnNesteRegistreringTilstandMed(status: Status): RegistreringTilstand?
    fun hentAntallPerStatus(): Map<String, Int>
    fun hentTilstandFor(registreringsId: Long): RegistreringTilstand
}