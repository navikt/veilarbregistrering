package no.nav.fo.veilarbregistrering.registrering.formidling

interface RegistreringTilstandRepository {
    fun lagre(registreringTilstand: RegistreringTilstand): Long
    fun oppdater(registreringTilstand: RegistreringTilstand): RegistreringTilstand
    fun hentRegistreringTilstand(id: Long): RegistreringTilstand
    fun finnRegistreringTilstanderMed(status: Status): List<RegistreringTilstand>
    fun finnNesteRegistreringTilstandMed(status: Status): RegistreringTilstand?
    fun finnXNesteRegistreringTilstanderMed(x: Int, status: Status): List<RegistreringTilstand>
    fun hentAntallPerStatus(): Map<Status, Int>
    fun hentTilstandFor(registreringsId: Long): RegistreringTilstand
}