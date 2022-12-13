package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface PopulerFoedselsnummerRepository {
    fun finnAktorIdTilRegistrertUtenFoedselsnummerMedGrense(
        maksAntall: Int,
        aktorIdDenyList: List<AktorId> = emptyList()
    ): List<AktorId>

    fun finnAktorIdTilRegistrertUtenFoedselsnummer(): List<AktorId>

    fun oppdaterRegistreringerMedManglendeFoedselsnummer(
        aktorIdFoedselsnummerMap: Map<AktorId, Foedselsnummer>
    ): IntArray
}