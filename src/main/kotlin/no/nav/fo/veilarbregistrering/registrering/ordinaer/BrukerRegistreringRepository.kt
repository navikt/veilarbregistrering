package no.nav.fo.veilarbregistrering.registrering.ordinaer

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.paw.arbeidssokerregisteret.intern.v1.OpplysningerOmArbeidssoekerMottatt

interface BrukerRegistreringRepository {
    fun lagre(registrering: OrdinaerBrukerRegistrering, bruker: Bruker): OrdinaerBrukerRegistrering
    fun hentBrukerregistreringForId(brukerregistreringId: Long): OrdinaerBrukerRegistrering
    fun hentBrukerregistreringForFoedselsnummer(foedselsnummerList: List<Foedselsnummer>): List<OrdinaerBrukerRegistrering>
    fun finnOrdinaerBrukerregistreringForAktorIdOgTilstand(aktorId: AktorId, tilstander: List<Status>): List<OrdinaerBrukerRegistrering>
    fun hentBrukerTilknyttet(brukerRegistreringId: Long): Bruker
    fun hentNesteOpplysningerOmArbeidssoeker(antall: Int): List<Pair<Long, OpplysningerOmArbeidssoekerMottatt>>
    fun settOpplysningerOmArbeidssoekerSomOverfort(listeMedIder: List<Int>)
}