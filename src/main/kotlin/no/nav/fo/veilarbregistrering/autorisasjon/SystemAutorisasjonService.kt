package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer


open class SystemAutorisasjonService(private val authContextHolder: AuthContextHolder) : AutorisasjonService {

    override fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkLesetilgangTilBruker()
    override fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) = sjekkSkrivetilgangTilBruker()

    private fun sjekkLesetilgangTilBruker() {
        if (!authContextHolder.erSystemBruker())
            throw UnsupportedOperationException("Denne strategien skal kun benyttes for ${UserRole.SYSTEM} - ikke ${authContextHolder.role}")
        return
    }

    private fun sjekkSkrivetilgangTilBruker() {
        throw UnsupportedOperationException("Det er ikke implementert noen støtte for skrivetilgang med rollen ${UserRole.SYSTEM}!")
    }

    override val innloggetVeilederIdent: String
        get() {
            throw UnsupportedOperationException("InnloggetVeilederIdent er ikke støttet med rollen ${UserRole.SYSTEM}")
        }

    override fun erVeileder(): Boolean = false
}