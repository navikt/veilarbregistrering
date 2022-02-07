package no.nav.fo.veilarbregistrering.registrering.formidling

class RegistreringTilstandService(private val registreringTilstandRepository: RegistreringTilstandRepository) {
    fun oppdaterRegistreringTilstand(command: OppdaterRegistreringTilstandCommand) {
        val original = registreringTilstandRepository.hentRegistreringTilstand(command.id)
        val oppdatert = original.oppdaterStatus(command.status)
        registreringTilstandRepository.oppdater(oppdatert)
    }

    fun finnRegistreringTilstandMed(status: Status?): List<RegistreringTilstand> {
        return registreringTilstandRepository.finnRegistreringTilstanderMed(status!!)
    }
}