package no.nav.fo.veilarbregistrering.registrering.formidling

interface RegistreringFormidlingRepository {
    fun lagre(registreringFormidling: RegistreringFormidling): Long
    fun oppdater(registreringFormidling: RegistreringFormidling): RegistreringFormidling
    fun hentRegistreringTilstand(id: Long): RegistreringFormidling
    fun finnRegistreringTilstanderMed(status: Status): List<RegistreringFormidling>
    fun finnNesteRegistreringTilstandMed(status: Status): RegistreringFormidling?
    fun hentAntallPerStatus(): Map<Status, Int>
    fun hentTilstandFor(registreringsId: Long): RegistreringFormidling
}